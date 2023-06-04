package com.example.frpeffsample.login

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.combine
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.sample
import com.example.frpeffsample.effects.Effects
import com.example.frpeffsample.navigation.RouterDomain

object LoginDomain {

    val loginClicked = Event.create<Unit>()

    val loginFx = Effects.loginFx()

    val storeLoginInProgress = Store.create(false)
        .on(loginClicked) { _, _ -> true }
        .on(loginFx.finally) { _, _ -> false }

    val passwordTextChanged = Event.create<String>()
    val storePasswordText = Store.create("")
        .on(passwordTextChanged) { _, text -> text }

    val loginTextChanged = Event.create<String>()
    val storeLoginText = Store.create("")
        .on(loginTextChanged) { _, text -> text }

    val storeLoginEnabled = combine(
        storeLoginText,
        storePasswordText,
        storeLoginInProgress
    ) { login, password, inProgress ->
        login.isNotBlank() && password.isNotBlank() && !inProgress
    }

    val storeError = Store.create("")
        .on(loginFx.fail) { _, error -> error.message ?: "" }
        .on(loginClicked) { _, _ -> "" }

    init {
        sample(
            clock = loginClicked,
            source = combine(storeLoginText, storePasswordText, ::Pair),
            target = loginFx,
        )
        sample(
            clock = loginFx.done,
            source = Store.create(RouterDomain.Screen.Weather),
            target = RouterDomain.screenUpdated,
        )
    }
}