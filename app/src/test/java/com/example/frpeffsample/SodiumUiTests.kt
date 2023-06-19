package com.example.frpeffsample

import com.example.frpeffsample.sodium.SignalsTodoList
import com.example.frpeffsample.sodium.SodiumTodoList
import org.junit.Assert
import org.junit.Test

class SodiumUiTests {

    @Test
    fun test() {
        SodiumTodoList.apply {
            fun assertUi(expected: String) = Assert.assertEquals(expected, sUI.sample())

            assertUi("((, []), false)")

            textChanged.send("item #1")
            textChanged.send("item #2")
            assertUi("((item #2, []), true)")

            addClicked.send(Unit)
            assertUi("((, [item #2]), false)")

            textChanged.send("item #3")
            addClicked.send(Unit)
            assertUi("((, [item #2, item #3]), false)")

            deleteItemClicked.send(1)
            assertUi("((, [item #2]), false)")
        }
    }

    @Test
    fun test2() {
        SignalsTodoList.apply {
            fun assertUi(expected: String) = Assert.assertEquals(expected, sUI.sample().toString())

            assertUi("((, []), false)")

            textChanged.send("item #1")
            textChanged.send("item #2")
            assertUi("((item #2, []), true)")

            addClicked.send(Unit)
            assertUi("((, [item #2]), false)")

            textChanged.send("item #3")
            addClicked.send(Unit)
            assertUi("((, [item #2, item #3]), false)")

            deleteItemClicked.send(1)
            assertUi("((, [item #2]), false)")
        }
    }
}