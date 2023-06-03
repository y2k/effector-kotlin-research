package com.example.frpeffsample.effector

interface Scope {
    fun <T> getState(stringStore: Store<T>): T
}

interface MockEffect
interface MockEvent

fun <T> mockEvent(e: Event<T>, value: T): MockEvent =
    TODO("FIXME")

fun <T, R> mockEffect(effect: Effect<T, R>, f: (T) -> R): MockEffect =
    TODO("FIXME")

fun fork(
    values: List<MockEvent> = emptyList(),
    handlers: List<MockEffect> = emptyList(),
): Scope {
    TODO("FIXME")
}

suspend fun <T> allSettled(e: Event<T>, param: T, scope: Scope) {
    TODO("FIXME")
}
