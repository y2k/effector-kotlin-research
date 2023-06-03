package com.example.frpeffsample.effector.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.frpeffsample.effector.Store
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> Store<T>.get(): T {
    val state: StateFlow<T> = this.asStateFlow()
    return state.collectAsState().value
}