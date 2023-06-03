package com.example.frpeffsample.weather

import com.example.frpeffsample.effector.Effect
import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.sample

val textChanged = Event.create<String>()

val textStore = Store.create("")
    .on(textChanged) { _, text -> text }

val searchClicked = Event.create<Unit>()

val weatherStore = Store.create("")

private val searchFx = Effect.create<String, String> {
    TODO("")
}

fun main() {
    sample(
        clock = searchClicked,
        source = textStore,
        target = searchFx,
    )
    sample(
        clock = searchFx.done,
        source = textStore,
        target = weatherStore,
    )
}
