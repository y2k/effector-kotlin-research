package com.example.frpeffsample.effects

import com.example.frpeffsample.effector.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object WeatherEffect {
    fun apiSearchFx() = Effect.create<String, String> { city ->
        withContext(Dispatchers.IO) {
            println("Load weather for city: $city")
            val json =
                URL("https://raw.githubusercontent.com/y2k/effector-kotlin-research/master/mock-weather-response.json")
                    .readText()
            json.replace("__CITY__", city)
        }
    }
}