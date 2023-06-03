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
    operator fun invoke(p: T)

    companion object {
        fun <T> create(): Event<T> =
            object : InnerEvent<T> {
                override val value: MutableSharedFlow<T> = MutableSharedFlow(
                    extraBufferCapacity = 1,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST,
                )

                override fun run(param: T) {
                    value.tryEmit(param)
                }

                override fun invoke(p: T) {
                    value.tryEmit(p)
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

                override val done = Event.create<R>()
                override val fail = Event.create<Exception>()

                @OptIn(DelicateCoroutinesApi::class)
                override fun run(param: T) {
                    GlobalScope.launch(Dispatchers.Main.immediate) {
                        try {
                            done(f(param))
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
    val internalStore = this as InnerStore<T>
    sample(
        source = this,
        clock = event,
        target = Effect.create<Any, Unit> {
            GlobalScope.launch(Dispatchers.Main) {
                internalStore.state.value = internalStore.initValueProp
            }
        },
    )
    return this
}
