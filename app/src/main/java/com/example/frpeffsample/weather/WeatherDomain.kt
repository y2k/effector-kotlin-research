package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.combine
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.reset
import com.example.frpeffsample.effector.sample
import com.example.frpeffsample.effects.Effects
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object WeatherDomain {

    val apiSearchFx = Effects.apiSearchFx()

    val searchClicked = Event.create<Unit>()

    val textChanged = Event.create<String>()

    val storeTemperature = Store.create("")
        .on(searchClicked) { _, _ -> "Loading..." }
        .on(apiSearchFx.done) { _, text ->
            val json = Json.decodeFromString<Response>(text)
            "${json.city}: ${json.temperature}"
        }
        .on(apiSearchFx.fail) { _, _ -> "Error" }
        .reset(textChanged)

    val storeCityText = Store.create("")
        .on(textChanged) { _, text -> text }

    val storeGetTempEnabled = combine(
        storeCityText,
        apiSearchFx.pending
    ) { text, inProgress -> text.length >= 3 && !inProgress }

    init {
        sample(
            clock = searchClicked,
            source = storeCityText,
            target = apiSearchFx,
        )
    }

    @Serializable
    data class Response(val city: String, val temperature: Int)
}