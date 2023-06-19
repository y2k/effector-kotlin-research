package com.example.frpeffsample.sodium.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.frpeffsample.sodium.SignalsTodoList
import com.example.frpeffsample.sodium.signals.collectAsState

@Composable
fun SignalsTodoListCompose() {
    val ui by SignalsTodoList.sUI.collectAsState()

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = ui.first.first,
            onValueChange = { SignalsTodoList.textChanged.send(it) }
        )
        Button(
            enabled = ui.second,
            onClick = { SignalsTodoList.addClicked.send(Unit) }) {
            Text(text = "Add")
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            for ((i, item) in ui.first.second.withIndex()) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        color = Color.White,
                        text = item
                    )
                    Button(onClick = { SignalsTodoList.deleteItemClicked.send(i) }) { Text(text = "Delete") }
                }
            }
        }
    }
}