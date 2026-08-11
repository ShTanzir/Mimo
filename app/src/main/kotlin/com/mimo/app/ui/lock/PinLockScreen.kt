package com.mimo.app.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mimo.app.util.Prefs
import kotlinx.coroutines.launch

/** Simple PIN gate shown before letting the user leave MIMO's rule screens, if enabled. */
@Composable
fun PinLockScreen(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter your MIMO PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) { pin = it; error = false } },
            singleLine = true,
            isError = error,
            visualTransformation = PasswordVisualTransformation(),
            supportingText = { if (error) Text("Incorrect PIN") }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                if (prefs.verifyPin(pin)) onUnlocked() else error = true
            }
        }) { Text("Unlock") }
    }
}
