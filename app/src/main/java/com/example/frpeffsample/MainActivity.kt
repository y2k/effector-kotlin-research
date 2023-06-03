package com.example.frpeffsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.frpeffsample.todo.compose.TodoList
import com.example.frpeffsample.ui.theme.FrpEffSampleTheme
import com.example.frpeffsample.weather.compose.WeatherList
import com.example.frpeffsample.todo.main as todoMain
import com.example.frpeffsample.weather.main as weatherMain

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrpEffSampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherList()
                }
            }
        }

        todoMain()
        weatherMain()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 520)
@Composable
fun GreetingPreview() {
    FrpEffSampleTheme {
        TodoList()
    }
}