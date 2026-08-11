package com.mimo.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mimo.app.ui.theme.Cream
import com.mimo.app.ui.theme.SageDark
import com.mimo.app.ui.theme.SageLight
import kotlinx.coroutines.launch

private data class OnboardPage(val icon: ImageVector, val title: String, val body: String)

private val pages = listOf(
    OnboardPage(
        Icons.Filled.Shield,
        "Meet MIMO",
        "Pick the apps that pull you in, and let MIMO gently close them for you."
    ),
    OnboardPage(
        Icons.Filled.HourglassBottom,
        "Set your own pace",
        "Immediate, 10 seconds, 1 minute, 5 minutes, or a custom delay — you decide how much room you need."
    ),
    OnboardPage(
        Icons.Filled.Insights,
        "See your progress",
        "MIMO keeps a simple log of every app it closed, so you can watch your habits shift over time."
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SageLight, SageDark)))
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(Modifier.height(48.dp))
            Text(
                "MIMO",
                color = Cream,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Mindful moments, one app at a time",
                color = Cream.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.weight(1f))

            HorizontalPager(state = pagerState, modifier = Modifier.weight(3f)) { page ->
                val p = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(p.icon, contentDescription = null, tint = Cream, modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(24.dp))
                    Text(
                        p.title,
                        color = Cream,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        p.body,
                        color = Cream.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .background(
                                Cream.copy(alpha = if (selected) 1f else 0.4f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cream, contentColor = SageDark)
            ) {
                Text(if (pagerState.currentPage < pages.lastIndex) "Next" else "Get started")
            }

            TextButton(onClick = onFinished, modifier = Modifier.fillMaxWidth()) {
                Text("Skip", color = Cream.copy(alpha = 0.8f))
            }
        }
    }
}
