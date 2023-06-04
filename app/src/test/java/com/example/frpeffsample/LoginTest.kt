package com.example.frpeffsample

import com.example.frpeffsample.effector.Scope
import com.example.frpeffsample.effector.TestScopeApi
import com.example.frpeffsample.effector.allSettled
import com.example.frpeffsample.effector.fork
import com.example.frpeffsample.login.LoginDomain
import com.example.frpeffsample.navigation.RouterDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, TestScopeApi::class)
class LoginTest {

    @Test
    fun test_loginClicked_failed() {
        val scope = fork(
            values = listOf(
                Scope.store(LoginDomain.storeLoginText, "user"),
                Scope.store(LoginDomain.storePasswordText, "wrong-pass"),
            ),
            handlers = listOf(Scope.effect(LoginDomain.loginFx) { error(it) })
        )

        allSettled(LoginDomain.loginClicked, Unit, scope)

        Assert.assertEquals("(user, wrong-pass)", scope.getState(LoginDomain.storeError))
    }

    @Test
    fun test_loginClicked_success() {
        val scope = fork(
            values = listOf(
                Scope.store(LoginDomain.storeLoginText, "user"),
                Scope.store(LoginDomain.storePasswordText, "pass"),
            ),
            handlers = listOf(Scope.effect(LoginDomain.loginFx) {})
        )

        allSettled(LoginDomain.loginClicked, Unit, scope)

        Assert.assertEquals(RouterDomain.Screen.Weather, scope.getState(RouterDomain.storeCurrentScreen))
    }

    @Before
    fun before() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun after() = Dispatchers.resetMain()
}