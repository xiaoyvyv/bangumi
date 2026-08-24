package com.xiaoyv.bangumi.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_name
import com.xiaoyv.bangumi.features.settings.business.SplashSideEffect
import com.xiaoyv.bangumi.features.settings.business.SplashState
import com.xiaoyv.bangumi.features.settings.business.SplashViewModel
import com.xiaoyv.bangumi.shared.component.Live2D
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    viewModel.collectBaseSideEffect { effect ->
        when (effect) {
            is SplashSideEffect.Navigate -> onNavScreen(effect.screen)
        }
    }

    /*    SplashScreen(
            state = baseState.data,
            onLaunch = { viewModel.onEvent(SplashEvent.Action.OnLaunch) },
        )*/
    Live2DSplash()
}

@Composable
fun Live2DSplash() {
    val live2DState = rememberLive2DState()

    Column(
        Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {

        Live2D(
            modifier = Modifier
                .systemBarsPadding()
                .padding(bottom = 80.dp)
                .width(200.dp)
                .aspectRatio(202 / 308f)
                .background(MaterialTheme.colorScheme.primaryContainer),
            state = live2DState
        )

        Button(onClick = {
            val modelName = "bangumi_musume_2d"
            val modelPath = Res.getUri("files/live2d/$modelName/$modelName.moc3")
            val modelJsonPath = Res.getUri("files/live2d/$modelName/$modelName.model3.json")
            val modelDir = modelPath.substringBeforeLast("/")
            debugLog { "modelDir:$modelDir, " }
            debugLog { "modelJsonPath:$modelDir, " }
            debugLog { "modelPath:$modelPath, " }
            live2DState.loadModel(modelDir, "$modelName.model3.json")
        }) {
            Text(text = "Load Live2D")
        }
    }
}

@Composable
fun SplashScreen(
    state: SplashState,
    onLaunch: () -> Unit,
) {
    LaunchedEffect(Unit) { onLaunch() }

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.primaryContainer.copy(alpha = 0.5f),
                        colorScheme.surface,
                    ),
                ),
            )
            .systemBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(colorScheme.surface.copy(alpha = 0.76f)),
            contentAlignment = Alignment.Center,
        ) {
            BgmProgressIndicator(
                modifier = Modifier.size(52.dp),
                color = colorScheme.primary,
                trackColor = colorScheme.primaryContainer,
            )
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
        )
    }
}
