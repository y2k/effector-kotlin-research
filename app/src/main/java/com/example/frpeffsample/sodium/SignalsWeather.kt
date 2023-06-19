package com.example.frpeffsample.sodium

import com.example.frpeffsample.sodium.signals.Signals
import com.example.frpeffsample.sodium.signals.attachStore
import nz.sodium.Cell
import nz.sodium.StreamSink

object SignalsWeather {

    object DownloadEffect {

        sealed interface Status {
            object Idle : Status
            object InProgress : Status
            data class Success(val data: String) : Status
            data class Error(val error: Exception) : Status
        }

        class Request(
            val url: String,
            val callback: (Result<String>) -> Unit
        )

        val downloadRequested = StreamSink<Request>()
        val storeResponse = Cell<Status>(Status.Idle)
    }

    // region Events

    val searchClicked = StreamSink<Unit>()
    val textChanged = StreamSink<String>()

    // endregion

    // region Stores

    private val storeCityText = Signals.createStore("") {
        on(textChanged) { _, text -> text }
    }

    private val storeGetTempEnabled = storeCityText.map { text -> text.length >= 3 }

    private val storeTemperature = Signals.createStore("") {
        on(searchClicked) { _, _ -> "Loading..." }
        on(textChanged) { _, _ -> "" }
    }

    val ui =
        storeCityText
            .attachStore(storeGetTempEnabled)
            .attachStore(storeTemperature)

    // endregion

    fun foo() {

        val a = searchClicked
            .snapshot(storeCityText)
            .map { DownloadEffect.Request("<url>$it", TODO()) }

    }
}