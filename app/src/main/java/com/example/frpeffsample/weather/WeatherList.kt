package com.example.frpeffsample.weather

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
            value = WeatherDomain.storeCityText.get(),
            onValueChange = { WeatherDomain.textChanged(it) }
        )
        Button(
            enabled = WeatherDomain.storeGetTempEnabled.get(),
            onClick = { WeatherDomain.searchClicked(Unit) }
        ) {
            Text(text = "Get temperature")
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = WeatherDomain.storeTemperature.get()
        )
    }
}