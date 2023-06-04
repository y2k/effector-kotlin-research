package com.example.frpeffsample.effects

import com.example.frpeffsample.effector.Effect
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

object LoginEffects {
    fun loginFx() = Effect.create<Pair<String, String>, Unit> { (user, pass) ->
        delay(1.seconds)
        if (user != "user" || pass != "pass")
            throw Exception("Login failed")
    }
}