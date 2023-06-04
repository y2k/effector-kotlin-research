package com.example.frpeffsample

import com.example.frpeffsample.effector.Scope
import com.example.frpeffsample.effector.TestScopeApi
import com.example.frpeffsample.effector.allSettled
import com.example.frpeffsample.effector.fork
import com.example.frpeffsample.weather.WeatherDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class, TestScopeApi::class)
class ExampleUnitTest {

    @Test
    fun test_searchClicked() {
        /* Создание локального скоупа с фейковыми эффектами и предустановлеными значениями сторов */
        val scope = fork(
            values = listOf(
                /* Выставление значения стора storeCityText для теста */
                Scope.store(WeatherDomain.storeCityText, "Tokyo")
            ),
            handlers = listOf(
                /* Перегрузка эффекта apiSearchFx на возврат фиксированного значения */
                Scope.effect(WeatherDomain.apiSearchFx) { """{ "city": "$it", "temperature": 25 }""" }
            )
        )

        /* Запуск события searchClicked в локальном скоупе и ожидание завершения всех эффектов */
        allSettled(WeatherDomain.searchClicked, Unit, scope)

        assertEquals("Tokyo: 25", scope.getState(WeatherDomain.storeTemperature))
    }

    @Before
    fun before() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun after() = Dispatchers.resetMain()
}