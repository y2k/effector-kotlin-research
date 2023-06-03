package com.example.frpeffsample.todo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frpeffsample.effector.compose.get
import com.example.frpeffsample.todo.addClicked
import com.example.frpeffsample.todo.itemDeleted
import com.example.frpeffsample.todo.storeAddEnabled
import com.example.frpeffsample.todo.storeItems
import com.example.frpeffsample.todo.storeText
import com.example.frpeffsample.todo.textChanged

@Composable
fun TodoList() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = storeText.get(),
            onValueChange = { textChanged(it) }
        )
        Button(
            enabled = storeAddEnabled.get(),
            onClick = { addClicked(Unit) }) {
            Text(text = "Add")
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            for ((i, item) in storeItems.get().withIndex()) {
                Row {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        text = item
                    )
                    Button(onClick = { itemDeleted(i) }) { Text(text = "Delete") }
                }
            }
        }
    }
}
