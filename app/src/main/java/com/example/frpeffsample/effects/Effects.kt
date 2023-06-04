package com.example.frpeffsample.effects

import com.example.frpeffsample.effector.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.time.Duration.Companion.seconds

object Effects {

    fun loginFx() = Effect.create<Pair<String, String>, Unit> { (user, pass) ->
        delay(1.seconds)
        if (user != "user" || pass != "pass")
            throw Exception("Incorrect user or password")
    }

    fun apiSearchFx() = Effect.create<String, String> { city ->
        withContext(Dispatchers.IO) {
            println("Load weather for city: $city")
            delay(1.seconds)
            val json =
                URL("https://raw.githubusercontent.com/y2k/effector-kotlin-research/master/mock-weather-response.json")
                    .readText()
            json.replace("__CITY__", city)
        }
    }
}