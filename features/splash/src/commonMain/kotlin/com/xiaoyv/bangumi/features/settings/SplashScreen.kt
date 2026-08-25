package com.xiaoyv.bangumi.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_name
import com.xiaoyv.bangumi.features.settings.business.SplashEvent
import com.xiaoyv.bangumi.features.settings.business.SplashSideEffect
import com.xiaoyv.bangumi.features.settings.business.SplashState
import com.xiaoyv.bangumi.features.settings.business.SplashViewModel
import com.xiaoyv.bangumi.shared.component.Live2D
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.joinToString
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.resource.copyToDir
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        SplashScreen(
            state = baseState.data,
            onLaunch = { viewModel.onEvent(SplashEvent.Action.OnLaunch) },
        )
//    Live2DSplash {
//        onNavScreen(Screen.PixivMain)
//    }
}

@Composable
fun Live2DSplash(onClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val live2DState = rememberLive2DState(
        onHitAreaClick = {
            debugLog { "OnHit:$it" }
        }
    )

    var isLoaded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var motions by remember { mutableStateOf<List<String>>(emptyList()) }
    var expressions by remember { mutableStateOf<List<String>>(emptyList()) }
    var activeMotion by remember { mutableStateOf<String?>(null) }
    var activeExpression by remember { mutableStateOf<String?>(null) }

    fun refreshMetadata() {
        motions = live2DState.getMotions()
        expressions = live2DState.getExpressions()
        debugLog { "Fetched motions (${motions.size}): ${motions.joinToString(",")}" }
        debugLog { "Fetched expressions (${expressions.size}): ${expressions.joinToString(",")}" }
    }

    fun loadModel() {
        if (isLoading) return
        isLoading = true
        scope.launch {
            try {
                val workDir = (FileKit.filesDir / "live2d").also {
                    it.createDirectories()
                }
                val name = "bangumi_black_musume_2026_parts"
                val targetFile = Res.copyToDir("files/live2d/$name.zip", workDir)
                val path = targetFile.absolutePath()
                live2DState.workDir = workDir.absolutePath()
                live2DState.loadModel(path, name)

                // 稍微延时等待 Native 渲染线程完成模型解压与初始化
                kotlinx.coroutines.delay(600)
                refreshMetadata()
                isLoaded = true
            } catch (e: Throwable) {
                debugLog { "Failed to load Live2D model: ${e.message}" }
            } finally {
                isLoading = false
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Title Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(colorScheme.primaryContainer.copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live2D 交互测试控制台",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isLoaded) Color(0xFF4CAF50) else colorScheme.outline.copy(alpha = 0.5f)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isLoaded) "已加载" else "未加载",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "包含完整的模型加载、触摸交互响应、动作与表情列表获取及点击播放功能。",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // --- Live2D Canvas Viewport ---
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .border(2.dp, colorScheme.primary.copy(alpha = 0.3f), MaterialTheme.shapes.large)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .width(180.dp)
                .aspectRatio(202 / 308f),
            contentAlignment = Alignment.Center
        ) {
            Live2D(
                modifier = Modifier.fillMaxSize(),
                state = live2DState
            )

            if (!isLoaded && !isLoading) {
                Text(
                    text = "点击下方按钮加载 Live2D 模型",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            if (isLoading) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BgmProgressIndicator(modifier = Modifier.size(36.dp))
                    Text(
                        text = "正在解压并渲染模型...",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.primary
                    )
                }
            }

            // Canvas touch hint badge
            if (isLoaded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "💡 可触控拖拽",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        // --- Action Buttons Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { loadModel() },
                enabled = !isLoading
            ) {
                Text(if (isLoaded) "重新加载模型" else "加载 Live2D 模型")
            }

            if (isLoaded) {
                Button(
                    onClick = { refreshMetadata() }
                ) {
                    Text("刷新动作与表情")
                }
            }
        }

        // --- Motions Section ---
        if (isLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(colorScheme.surface)
                    .border(1.dp, colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎬 动作列表 (Motions: ${motions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    if (motions.isNotEmpty()) {
                        Button(
                            onClick = {
                                val randomGroup = motions.random()
                                live2DState.setMotion(randomGroup, 0)
                                activeMotion = randomGroup
                            }
                        ) {
                            Text("🎲 随机动作", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (motions.isEmpty()) {
                    Text(
                        text = "暂未获取到动作列表，点击“刷新动作与表情”重试",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.error
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        motions.forEach { group ->
                            val isSelected = activeMotion == group
                            Button(
                                onClick = {
                                    live2DState.setMotion(group, 0)
                                    activeMotion = group
                                }
                            ) {
                                Text(
                                    text = if (isSelected) "▶ $group" else group,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // --- Expressions Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(colorScheme.surface)
                    .border(1.dp, colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "😊 表情列表 (Expressions: ${expressions.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                if (expressions.isEmpty()) {
                    Text(
                        text = "暂未获取到表情列表，点击“刷新动作与表情”重试",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.error
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        expressions.forEach { expr ->
                            val isSelected = activeExpression == expr
                            Button(
                                onClick = {
                                    live2DState.setExpression(expr)
                                    activeExpression = expr
                                }
                            ) {
                                Text(
                                    text = if (isSelected) "✨ $expr" else expr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // --- Current Status Summary Card ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "当前动作: ${activeMotion ?: "未切换 (Idle)"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "当前表情: ${activeExpression ?: "默认"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- Navigation Button ---
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "路由至其它页面")
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
