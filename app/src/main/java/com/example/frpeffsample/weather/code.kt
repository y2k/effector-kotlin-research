package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.map
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.reset
import com.example.frpeffsample.effector.sample
import com.example.frpeffsample.effects.WeatherEffect
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val apiSearchFx = WeatherEffect.apiSearchFx()

val textChanged = Event.create<String>()

val searchClicked = Event.create<Unit>()

val storeCityText = Store.create("")
    .on(textChanged) { _, text -> text }

val storeGetTempEnabled = storeCityText.map { it.length >= 3 }

val storeTemperature = Store.create("")
    .on(searchClicked) { _, _ -> "Loading..." }
    .on(apiSearchFx.done) { _, text ->
        val json = Json.decodeFromString<Response>(text)
        "${json.city}: ${json.temperature}"
    }
    .on(apiSearchFx.fail) { _, _ -> "Error" }
    .reset(textChanged)

val main = run {
    sample(
        clock = searchClicked,
        source = storeCityText,
        target = apiSearchFx,
    )
}

@Serializable
data class Response(val city: String, val temperature: Int)
