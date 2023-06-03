package com.example.frpeffsample.weather.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.frpeffsample.weather.searchClicked
import com.example.frpeffsample.weather.storeCityText
import com.example.frpeffsample.weather.storeGetTempEnabled
import com.example.frpeffsample.weather.storeTemperature
import com.example.frpeffsample.weather.textChanged

@Composable
fun WeatherList() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = storeCityText.get(),
            onValueChange = { textChanged(it) }
        )
        Button(
            enabled = storeGetTempEnabled.get(),
            onClick = { searchClicked(Unit) }
        ) {
            Text(text = "Get temperature")
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = storeTemperature.get()
        )
    }
}