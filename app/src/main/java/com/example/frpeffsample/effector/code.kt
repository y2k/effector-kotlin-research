package com.example.frpeffsample.effector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private interface InnerStore<T> : Store<T>, InnerTarget<T> {
    val initValueProp: T
    val state: MutableStateFlow<T>
}

interface Store<T> : Target<T> {

    fun asStateFlow(): StateFlow<T>

    companion object {
        fun <T> create(initValue: T): Store<T> =
            object : InnerStore<T> {
                override val initValueProp = initValue
                override val state: MutableStateFlow<T> = MutableStateFlow(initValue)
                override fun asStateFlow(): StateFlow<T> = state
                override fun run(param: T) {
                    println("EFFECTOR: update store, with: $param")

                    state.value = param
                }
            }
    }
}

fun <T, R> Store<T>.map(f: (T) -> R): Store<R> {
    val currentStore = this as InnerStore<T>
    val store = Store.create(f(currentStore.state.value)) as InnerStore<R>

    currentStore.state
        .onEach { store.run(f(it)) }
        .launchIn(CoroutineScope(Unconfined))

    return store
}

private interface InnerEvent<T> : Event<T>, InnerTarget<T> {
    val value: MutableSharedFlow<T>
}

interface Event<T> : Target<T> {
    operator fun invoke(param: T)

    companion object {
        fun <T> create(): Event<T> =
            object : InnerEvent<T> {
                override val value: MutableSharedFlow<T> = MutableSharedFlow(
                    extraBufferCapacity = 1,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST,
                )

                override fun run(param: T) {
                    invoke(param)
                }

                override fun invoke(param: T) {
                    println("EFFECTOR: run event, param: $param")

                    value.tryEmit(param)
                }
            }
    }
}

private interface InnerEffect<T, R> : Effect<T, R>, InnerTarget<T> {
    override fun run(param: T)
}

interface Effect<T, R> : Target<T> {

    val done: Event<R>
    val fail: Event<Exception>

    companion object {
        fun <T, R> create(f: suspend (T) -> R): Effect<T, R> =
            object : InnerEffect<T, R> {

                private val key: Effect<*, *> = this

                override val done = Event.create<R>()
                override val fail = Event.create<Exception>()

                @OptIn(DelicateCoroutinesApi::class)
                override fun run(param: T) {
                    GlobalScope.launch(Unconfined) {
                        try {
                            val handlers = (localScope.get() as InnerScope?)?.handlers

                            @Suppress("UNCHECKED_CAST")
                            val mf = handlers?.get(key) as? suspend (T) -> R
                            val f2 = mf ?: f

                            println("EFFECTOR: run effect, param: $param")

                            done(f2(param))
                        } catch (e: Exception) {
                            fail(e)
                        }
                    }
                }
            }
    }
}

interface InnerTarget<T> : Target<T> {
    fun run(param: T)
}

interface Target<T>

@Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
fun <TS, TC, TT> sample(
    clock: Event<TC>,
    source: Store<TS>,
    target: Target<TT>,
    fn: (TS, TC) -> TT = { srcValue, _ -> srcValue as TT }
) {
    val clock = clock as InnerEvent<TC>
    val source = source as InnerStore<TS>
    val target = target as InnerTarget<TT>

    clock.value
        .onEach { clockValue ->
            val srcValue = source.state.value
            val result = fn(srcValue, clockValue)
            target.run(result)
        }
        .launchIn(CoroutineScope(Unconfined))
}

fun <T, TE> Store<T>.on(event: Event<TE>, f: (T, TE) -> T): Store<T> {
    sample(
        clock = event,
        source = this,
        fn = { srcValue, clockValue -> f(srcValue, clockValue) },
        target = this,
    )
    return this
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> Store<T>.reset(event: Event<*>): Store<T> {
    val privateStore = this as InnerStore<T>
    sample(
        source = this,
        clock = event,
        target = Effect.create<Any, Unit> {
            GlobalScope.launch(Dispatchers.Main) {
                privateStore.state.value = privateStore.initValueProp
            }
        },
    )
    return this
}

// region Scope

private class InnerScope(
    val values: List<MockStore> = emptyList(),
    val handlers: Map<Effect<*, *>, suspend (Any) -> Any>,
    private var intiaited: Boolean = false,
) : Scope {

    fun initialize() {
        if (intiaited) return
        intiaited = true

        @Suppress("NAME_SHADOWING")
        values.forEach { store ->
            val store = store as InnerMockStore
            store.originStore.run(store.value)
        }
    }

    override fun <T> getState(store: Store<T>): T {
        initialize()
        return (store as InnerStore).state.value
    }
}

private val localScope = ThreadLocal<Scope>()

interface Scope {
    fun <T> getState(store: Store<T>): T

    companion object {
        fun <T> store(store: Store<T>, value: T): MockStore =
            object : InnerMockStore {
                @Suppress("UNCHECKED_CAST")
                override val originStore: InnerStore<Any> = store as InnerStore<Any>
                override val value = value as Any
            }

        fun <T, R> effect(effect: Effect<T, R>, f: suspend (T) -> R): MockEffect =
            object : InnerMockEffect {
                override val originEffect: Effect<*, *> = effect

                @Suppress("UNCHECKED_CAST")
                override val fn: suspend (Any) -> Any = { f(it as T) as Any }
            }
    }
}

private interface InnerMockEffect : MockEffect {
    val originEffect: Effect<*, *>
    val fn: suspend (Any) -> Any
}

private interface InnerMockStore : MockStore {
    val originStore: InnerStore<Any>
    val value: Any
}

interface MockEffect
interface MockStore

fun fork(
    values: List<MockStore> = emptyList(),
    handlers: List<MockEffect> = emptyList(),
): Scope = InnerScope(
    values,
    handlers.map { it as InnerMockEffect }.associate { it.originEffect to it.fn }
)

fun <T> allSettled(event: Event<T>, param: T, scope: Scope) {
    localScope.set(scope)
    try {
        (scope as InnerScope).initialize()
        event(param)
    } finally {
        localScope.remove()
    }
}

// endregion
