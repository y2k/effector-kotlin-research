package com.example.frpeffsample

import com.example.frpeffsample.effector.allSettled
import com.example.frpeffsample.effector.fork
import com.example.frpeffsample.effector.mockEffect
import com.example.frpeffsample.effector.mockEvent
import com.example.frpeffsample.weather.searchClicked
import com.example.frpeffsample.weather.searchFx
import com.example.frpeffsample.weather.storeTemperature
import com.example.frpeffsample.weather.textChanged
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun test_textChanged() = runBlocking {
        val scope = fork(
            values = listOf(mockEvent(textChanged, "belgrade")),
            handlers = listOf(mockEffect(searchFx) { """{"temperature": 25}""" })
        )

        allSettled(
            searchClicked,
            Unit,
            scope
        )

        Assert.assertEquals("25", scope.getState(storeTemperature))
    }
}