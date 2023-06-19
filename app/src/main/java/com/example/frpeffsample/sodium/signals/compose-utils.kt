package com.example.frpeffsample.sodium.signals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import nz.sodium.Cell

@Composable
fun <T> Cell<T>.collectAsState(): State<T> {
    val uiFlow = remember {
        MutableStateFlow(sample())
    }
    LaunchedEffect(Unit) {
        val listener = listen { uiFlow.value = it }
        try {
            awaitCancellation()
        } finally {
            listener.unlisten()
        }
    }

    return uiFlow.collectAsState()
}