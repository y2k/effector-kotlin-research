package com.example.frpeffsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frpeffsample.effector.compose.get
import com.example.frpeffsample.login.LoginScreen
import com.example.frpeffsample.navigation.RouterDomain
import com.example.frpeffsample.todo.compose.TodoList
import com.example.frpeffsample.ui.theme.FrpEffSampleTheme
import com.example.frpeffsample.weather.WeatherList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrpEffSampleTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                ) {
                    when (RouterDomain.storeCurrentScreen.get()) {
                        RouterDomain.Screen.Login -> LoginScreen()
                        RouterDomain.Screen.Weather -> WeatherList()
                        RouterDomain.Screen.TodoList -> TodoList()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 520)
@Composable
fun GreetingPreview() {
    FrpEffSampleTheme {
        TodoList()
    }
}
