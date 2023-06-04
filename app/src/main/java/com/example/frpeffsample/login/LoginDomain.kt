package com.example.frpeffsample.login

import com.example.frpeffsample.effector.Event
import com.example.frpeffsample.effector.Store
import com.example.frpeffsample.effector.combine
import com.example.frpeffsample.effector.on
import com.example.frpeffsample.effector.reset
import com.example.frpeffsample.effector.sample
import com.example.frpeffsample.login.effects.LoginEffects

object LoginDomain {

    val loginTextChanged = Event.create<String>()
    val storeLoginText = Store.create("")
        .on(loginTextChanged) { _, text -> text }
        .reset(LoginEffects.loginFx.done)

    val passwordTextChanged = Event.create<String>()
    val storePasswordText = Store.create("")
        .on(passwordTextChanged) { _, text -> text }
        .reset(LoginEffects.loginFx.done)

    val loginClicked = Event.create<Unit>()

    val storeLoginInProgress = Store.create(false)
        .on(loginClicked) { _, _ -> true }
        .on(LoginEffects.loginFx.finally) { _, _ -> false }

    val loginEnabled = combine(
        storeLoginText,
        storePasswordText,
        storeLoginInProgress
    ) { login, password, inProgress ->
        login.isNotBlank() && password.isNotBlank() && !inProgress
    }

    private val loginWithPassword = combine(
        storeLoginText,
        storePasswordText
    ) { login, password -> login to password }

    init {
        sample(
            clock = loginClicked,
            source = loginWithPassword,
            target = LoginEffects.loginFx,
        )
    }
}