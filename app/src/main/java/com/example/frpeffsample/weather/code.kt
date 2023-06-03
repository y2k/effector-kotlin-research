package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Effect
import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.sample

val textChanged = Event.create<String>()

val storeCityText = Store.create("")
    .on(textChanged) { _, text -> text }

val searchClicked = Event.create<Unit>()

val storeTemperature = Store.create("")

private val searchFx = Effect.create<String, String> {
    TODO("")
}

fun main() {
    sample(
        clock = searchClicked,
        source = storeCityText,
        target = searchFx,
    )
    sample(
        clock = searchFx.done,
        source = storeCityText,
        target = storeTemperature,
    )
}
