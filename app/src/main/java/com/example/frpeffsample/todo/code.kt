package com.example.frpeffsample.todo

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.map
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.reset
import com.example.frpeffsample.effector.sample

val addClicked = Event.create<Unit>()
val textChanged = Event.create<String>()
val itemDeleted = Event.create<Int>()

private val itemAdded = Event.create<String>()

val storeText = Store.create("")
    .on(textChanged) { _, newText -> newText }
    .reset(itemAdded)

val storeAddEnabled = storeText.map { it.isNotEmpty() }

val storeItems = Store.create(emptyList<String>())
    .on(itemAdded) { items, newItem -> items + newItem }
    .on(itemDeleted) { items, position -> items.filterIndexed { i, _ -> i != position } }

val main = run {
    sample(
        clock = addClicked,
        source = storeText,
        target = itemAdded,
    )
}
