package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Effect
import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.sample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

val textChanged = Event.create<String>()

val storeCityText = Store.create("")
    .on(textChanged) { _, text -> text }

val searchClicked = Event.create<Unit>()

private val searchFx = Effect.create<String, String> {
    withContext(Dispatchers.IO) {
        URL("https://raw.githubusercontent.com/y2k/effector-kotlin-research/master/mock-weather-response.json")
            .readText()
    }
}

val storeTemperature = Store.create("...")
    .on(searchFx.done) { _, text -> Json.decodeFromString<Response>(text).temperature.toString() }

fun main() {
    sample(
        clock = searchClicked,
        source = storeCityText,
        target = searchFx,
    )
}

@Serializable
data class Response(val city: String, val temperature: Int)
