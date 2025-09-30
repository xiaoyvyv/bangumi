package com.xiaoyv.bangumi.features.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


@Composable
fun SplashRoute(onNavScreen: (Screen) -> Unit) {
    SplashScreen(onNavScreen = onNavScreen)
}

@Composable
fun SplashScreen(onNavScreen: (Screen) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

    }

    LaunchedEffect(Unit) {
        onNavScreen(Screen.Main)
    }
}