package com.example.frpeffsample.sodium.signals

import nz.sodium.Cell
import nz.sodium.Stream

object Signals {

    interface StoreBuilder<T> {
        fun <E> on(e: Stream<E>, f: (E, T) -> T)
        fun <E, C> on2(e: Stream<E>, c: Cell<C>, f: (E, C, T) -> T)
    }

    fun <T> createStore(defValue: T, f: StoreBuilder<T>. () -> Unit): Cell<T> {
        val listOf: List<Stream<(T) -> T>> = buildList {
            f(object : StoreBuilder<T> {
                override fun <E> on(e: Stream<E>, f: (E, T) -> T) {
                    val a: Stream<(T) -> T> = e.map { eventValue -> { f(eventValue, it) } }
                    add(a)
                }

                override fun <E, C> on2(e: Stream<E>, c: Cell<C>, f: (E, C, T) -> T) {
                    val element: Stream<(T) -> T> =
                        e.snapshot(c) { a, b -> { x -> f(a, b, x) } }
                    add(element)
                }
            })
        }
        return listOf
            .let { Stream.merge(it) { _, _ -> throw IllegalStateException() } }
            .accum(defValue) { f, s -> f(s) }
    }
}

fun <T1, T> Cell<T1>.attachStore(defValue: T, f: Signals.StoreBuilder<T>. () -> Unit): Cell<Pair<T1, T>> =
    lift(Signals.createStore(defValue, f), ::Pair)

fun <T1, T> Cell<T1>.attachStore(other: Cell<T>): Cell<Pair<T1, T>> = lift(other, ::Pair)