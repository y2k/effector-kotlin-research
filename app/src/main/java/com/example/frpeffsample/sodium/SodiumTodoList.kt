package com.example.frpeffsample.sodium

import nz.sodium.Cell
import nz.sodium.Stream
import nz.sodium.StreamSink

object SodiumTodoList {

    val textChanged = StreamSink<String>()
    val addClicked = StreamSink<Unit>()
    val deleteItemClicked = StreamSink<Int>()

    val sUI: Cell<String> = run {
        val sText: Cell<String> =
            listOf(
                textChanged.map { newText -> { _: String -> newText } },
                addClicked.map { { _: String -> "" } }
            )
                .let { Stream.merge(it) { _, _ -> throw IllegalStateException() } }
                .accum("") { f, s -> f(s) }

        sText
            .lift(
                listOf(
                    addClicked.snapshot(sText) { _, text -> { items: List<String> -> items + text } },
                    deleteItemClicked.map { index -> { items: List<String> -> items.filterIndexed { i, _ -> index != i } } }
                )
                    .let { Stream.merge(it) { _, _ -> throw IllegalStateException() } }
                    .accum(emptyList<String>()) { f, s -> f(s) }, ::Pair
            )
            .lift(sText.map { it.isNotEmpty() }, ::Pair)
            .map { it.toString() }
    }
}