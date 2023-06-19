package com.example.frpeffsample.sodium

import com.example.frpeffsample.sodium.signals.Signals
import com.example.frpeffsample.sodium.signals.attachStore
import nz.sodium.Cell
import nz.sodium.StreamSink

object SignalsTodoList {

    val textChanged = StreamSink<String>()
    val addClicked = StreamSink<Unit>()
    val deleteItemClicked = StreamSink<Int>()

    val sUI = run {
        val sText: Cell<String> =
            Signals.createStore("") {
                on(textChanged) { newText, _ -> newText }
                on(addClicked) { _, _ -> "" }
            }

        sText
            .attachStore(emptyList<String>()) {
                on(deleteItemClicked) { i, items -> items.filterIndexed { index, _ -> index != i } }
                on2(addClicked, sText) { _, text, items -> items + text }
            }
            .attachStore(sText.map(String::isNotEmpty))
    }
}