package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Effect
import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.map
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.reset
import com.example.frpeffsample.effector.sample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

val textChanged = Event.create<String>()

val searchClicked = Event.create<Unit>()

val storeCityText = Store.create("")
    .on(textChanged) { _, text -> text }

val storeGetTempEnabled = storeCityText.map { it.length >= 3 }

internal val apiSearchFx = Effect.create<String, String> { city ->
    withContext(Dispatchers.IO) {
        println("Load weather for city: $city")
        val json =
            URL("https://raw.githubusercontent.com/y2k/effector-kotlin-research/master/mock-weather-response.json")
                .readText()
        json.replace("__CITY__", city)
    }
}

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
