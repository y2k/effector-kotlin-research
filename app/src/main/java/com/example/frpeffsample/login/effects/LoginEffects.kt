package com.example.frpeffsample.login.effects

import com.example.frpeffsample.effector.Effect
import kotlinx.coroutines.delay

object LoginEffects {
    val loginFx = Effect.create<Pair<String, String>, Unit> {
        println("LOG | loginFx: $it")
        delay(2_000)
    }
}
