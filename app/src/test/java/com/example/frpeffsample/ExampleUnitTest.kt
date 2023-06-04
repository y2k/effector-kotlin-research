package com.example.frpeffsample

import com.example.frpeffsample.effector.Scope
import com.example.frpeffsample.effector.allSettled
import com.example.frpeffsample.effector.fork
import com.example.frpeffsample.weather.apiSearchFx
import com.example.frpeffsample.weather.main
import com.example.frpeffsample.weather.searchClicked
import com.example.frpeffsample.weather.storeCityText
import com.example.frpeffsample.weather.storeTemperature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    @Test
    fun test_searchClicked() {
        val scope = fork(
            values = listOf(
                Scope.store(storeCityText, "Tokyo")
            ),
            handlers = listOf(
                Scope.effect(apiSearchFx) { """{ "city": "$it", "temperature": 25 }""" }
            )
        )

        allSettled(searchClicked, Unit, scope)

        assertEquals("Tokyo: 25", scope.getState(storeTemperature))
    }

    @Before
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        main()
    }

    @After
    fun after() = Dispatchers.resetMain()
}
