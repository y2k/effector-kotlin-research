package com.example.frpeffsample

import com.example.frpeffsample.effector.Scope
import com.example.frpeffsample.effector.allSettled
import com.example.frpeffsample.effector.fork
import com.example.frpeffsample.todo.addClicked
import com.example.frpeffsample.todo.itemDeleted
import com.example.frpeffsample.todo.storeItems
import com.example.frpeffsample.todo.storeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListTest {

    @Test
    fun test_addClicked() {
        val scope = fork(values = listOf(Scope.store(storeText, "item1")))

        allSettled(addClicked, Unit, scope)

        Assert.assertEquals(listOf("item1"), scope.getState(storeItems))
    }

    @Test
    fun test_itemDeleted() {
        val scope = fork(values = listOf(Scope.store(storeItems, listOf("item1"))))

        Assert.assertEquals(listOf("item1"), scope.getState(storeItems))

        allSettled(itemDeleted, 0, scope)

        Assert.assertEquals(emptyList<String>(), scope.getState(storeItems))
    }

    @Before
    fun before() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun after() = Dispatchers.resetMain()
}