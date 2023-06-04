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

    val storeError = Store.create("")
        .on(LoginEffects.loginFx.fail) { _, error -> error.message ?: "" }
        .on(loginClicked) { _, _ -> "" }

    val storeLoginInProgress = Store.create(false)
        .on(loginClicked) { _, _ -> true }
        .on(LoginEffects.loginFx.finally) { _, _ -> false }

    val storeLoginEnabled = combine(
        storeLoginText,
        storePasswordText,
        storeLoginInProgress
    ) { login, password, inProgress ->
        login.isNotBlank() && password.isNotBlank() && !inProgress
    }

    init {
        sample(
            clock = loginClicked,
            source = combine(storeLoginText, storePasswordText, ::Pair),
            target = LoginEffects.loginFx,
        )
    }
}
