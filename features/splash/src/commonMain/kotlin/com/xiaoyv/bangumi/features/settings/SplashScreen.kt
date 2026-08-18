package com.xiaoyv.bangumi.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import com.xiaoyv.bangumi.features.settings.business.SplashState
import com.xiaoyv.bangumi.features.settings.business.SplashViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    SideEffect(Unit) {
        onNavScreen(Screen.DnsResolver)
    }

    SplashScreen(
        state = baseState.data,
    )
}

@Composable
fun SplashScreen(state: SplashState) {

}