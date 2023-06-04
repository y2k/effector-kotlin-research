package com.example.frpeffsample.login

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
fun LoginScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !LoginDomain.storeLoginInProgress.get(),
            value = LoginDomain.storeLoginText.get(),
            onValueChange = { LoginDomain.loginTextChanged(it) }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !LoginDomain.storeLoginInProgress.get(),
            value = LoginDomain.storePasswordText.get(),
            onValueChange = { LoginDomain.passwordTextChanged(it) }
        )
        Button(
            enabled = LoginDomain.loginEnabled.get(),
            onClick = { LoginDomain.loginClicked(Unit) }
        ) {
            Text(text = if (LoginDomain.storeLoginInProgress.get()) "Logging in..." else "Login")
        }
    }
}
