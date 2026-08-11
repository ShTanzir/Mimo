package com.mimo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mimo.app.ui.navigation.MimoNavGraph
import com.mimo.app.ui.navigation.Routes
import com.mimo.app.ui.theme.MimoTheme
import com.mimo.app.util.Prefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MimoApp()
        }
    }
}

@Composable
private fun MimoApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { Prefs(context) }
    val darkTheme by prefs.darkTheme.collectAsState(initial = false)
    val onboardingDone by prefs.onboardingDone.collectAsState(initial = null)

    MimoTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (onboardingDone) {
                null -> { /* loading prefs, show nothing briefly */ }
                false -> MimoNavGraph(startDestination = Routes.ONBOARDING)
                true -> MimoNavGraph(startDestination = Routes.APP_LIST)
            }
        }
    }
}
