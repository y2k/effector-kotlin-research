package com.example.frpeffsample.navigation

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.on

object RouterDomain {

    enum class Screen { Login, Weather, TodoList }

    val screenUpdated = Event.create<Screen>()

    val storeCurrentScreen = Store.create(Screen.Login)
        .on(screenUpdated) { _, screen -> screen }
}