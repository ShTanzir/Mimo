package com.mimo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mimo.app.ui.navigation.MimoNavGraph
import com.mimo.app.ui.navigation.Routes
import com.mimo.app.ui.permissions.PermissionsScreen
import com.mimo.app.ui.theme.MimoTheme
import com.mimo.app.util.PermissionUtils
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

/**
 * Root composable. Beyond onboarding, MIMO re-checks its critical
 * permissions every time the app resumes (e.g. after the user comes back
 * from Settings) and keeps showing the Permissions screen — instead of the
 * app list — until everything required is granted. This mirrors what the
 * person asked for: the permission screen should keep reappearing rather
 * than letting the app silently stop working.
 */
@Composable
private fun MimoApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { Prefs(context) }
    val darkTheme by prefs.darkTheme.collectAsState(initial = false)
    val onboardingDone by prefs.onboardingDone.collectAsState(initial = null)

    var permissionsOk by remember { mutableStateOf(PermissionUtils.allCriticalGranted(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionsOk = PermissionUtils.allCriticalGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    MimoTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = Triple(onboardingDone, permissionsOk, Unit),
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
                label = "app-root"
            ) { (done, granted, _) ->
                when {
                    done == null -> { /* still loading prefs */ }
                    done == false -> MimoNavGraph(startDestination = Routes.ONBOARDING)
                    !granted -> PermissionsScreen(onContinue = { permissionsOk = true })
                    else -> MimoNavGraph(startDestination = Routes.APP_LIST)
                }
            }
        }
    }
}
