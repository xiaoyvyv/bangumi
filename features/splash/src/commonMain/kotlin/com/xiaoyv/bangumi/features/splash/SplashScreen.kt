package com.xiaoyv.bangumi.features.splash

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.splash_address_pending
import com.xiaoyv.bangumi.core_resource.resources.splash_enter_home
import com.xiaoyv.bangumi.core_resource.resources.splash_node_queued
import com.xiaoyv.bangumi.core_resource.resources.splash_node_resolved
import com.xiaoyv.bangumi.core_resource.resources.splash_node_resolving
import com.xiaoyv.bangumi.core_resource.resources.splash_node_safe_cache
import com.xiaoyv.bangumi.core_resource.resources.splash_online
import com.xiaoyv.bangumi.core_resource.resources.splash_route_initializer
import com.xiaoyv.bangumi.core_resource.resources.splash_route_sync
import com.xiaoyv.bangumi.core_resource.resources.splash_secure_dns_description
import com.xiaoyv.bangumi.core_resource.resources.splash_status_connecting
import com.xiaoyv.bangumi.core_resource.resources.splash_status_resolving
import com.xiaoyv.bangumi.core_resource.resources.splash_status_route_updated
import com.xiaoyv.bangumi.core_resource.resources.splash_status_safe_cache
import com.xiaoyv.bangumi.core_resource.resources.splash_title
import com.xiaoyv.bangumi.features.splash.business.DnsNodeState
import com.xiaoyv.bangumi.features.splash.business.DnsNodeStatus
import com.xiaoyv.bangumi.features.splash.business.SplashEvent
import com.xiaoyv.bangumi.features.splash.business.SplashSideEffect
import com.xiaoyv.bangumi.features.splash.business.SplashState
import com.xiaoyv.bangumi.features.splash.business.SplashViewModel
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.PullToRefreshBox
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

private val SpaceBlack = Color(0xFF03090D)
private val DeepTeal = Color(0xFF071A1D)
private val SignalCyan = Color(0xFF63F5D2)
private val SignalBlue = Color(0xFF54A9FF)
private val SignalAmber = Color(0xFFFFC857)
private val MutedText = Color(0xFF76969A)

@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    viewModel.collectBaseSideEffect { effect ->
        when (effect) {
            SplashSideEffect.NavigateMain -> onNavScreen(Screen.Main)
        }
    }

    SplashScreen(
        state = baseState.data,
        onLaunch = { viewModel.onEvent(SplashEvent.Action.OnLaunch) },
        onRefresh = { viewModel.onEvent(SplashEvent.Action.OnRefresh) },
    )
}

@Composable
fun SplashScreen(
    state: SplashState,
    onLaunch: () -> Unit,
    onRefresh: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "network-scan")
    val scanProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3_200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "scan-progress",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "core-pulse",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(DeepTeal, SpaceBlack),
                    radius = 1_300f,
                )
            )
    ) {
        TechGrid(scanProgress)

        PullToRefreshBox(
            isRefreshing = state.isResolving,
            onRefresh = onRefresh,
            enabled = !state.isResolving,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProtocolHeader()
                Spacer(Modifier.height(22.dp))
                ResolverCore(progress = state.progress, pulse = pulse)
                Spacer(Modifier.height(20.dp))
                ResolveSummary(state = state, onLaunch = onLaunch)
                Spacer(Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.nodes.forEach { node ->
                        DnsNodeCard(node)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(Res.string.splash_secure_dns_description),
                    color = MutedText.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.4.sp,
                )
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun ProtocolHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(Res.string.splash_title),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            )
            Text(
                text = stringResource(Res.string.splash_route_initializer),
                color = SignalCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 1.6.sp,
            )
        }

        Row(
            modifier = Modifier
                .border(1.dp, SignalCyan.copy(alpha = 0.45f), RoundedCornerShape(50))
                .background(SignalCyan.copy(alpha = 0.08f), RoundedCornerShape(50))
                .padding(horizontal = 11.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(Modifier.size(6.dp).background(SignalCyan, CircleShape))
            Text(
                text = stringResource(Res.string.splash_online),
                color = SignalCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun ResolverCore(progress: Float, pulse: Float) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(142.dp)) {
            val center = this.center
            val radius = size.minDimension / 2
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SignalCyan.copy(alpha = 0.16f * pulse), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
            )
            drawCircle(
                color = SignalCyan.copy(alpha = 0.18f),
                radius = radius * 0.77f,
                style = Stroke(width = 1.dp.toPx()),
            )
            drawArc(
                color = SignalCyan,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(radius * 0.23f, radius * 0.23f),
                size = Size(radius * 1.54f, radius * 1.54f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )
            drawCircle(
                color = SignalBlue.copy(alpha = 0.5f),
                radius = radius * 0.48f * pulse,
                style = Stroke(width = 1.dp.toPx()),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.splash_route_sync),
                color = SignalCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
            )
        }
    }
}

@Composable
private fun ResolveSummary(
    state: SplashState,
    onLaunch: () -> Unit,
) {
    val status = when {
        state.isComplete && state.failureCount > 0 -> stringResource(Res.string.splash_status_safe_cache)
        state.isComplete -> stringResource(Res.string.splash_status_route_updated)
        state.activeHostname.isNotBlank() -> stringResource(
            Res.string.splash_status_resolving,
            state.activeHostname,
        )

        else -> stringResource(Res.string.splash_status_connecting)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = status,
                color = if (state.failureCount > 0) SignalAmber else SignalCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${state.completedCount.toString().padStart(2, '0')} / ${state.nodes.size.toString().padStart(2, '0')}",
                color = MutedText,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
        }
        Spacer(Modifier.height(9.dp))
        LinearProgressIndicator(
            progress = { state.progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = SignalCyan,
            trackColor = Color.White.copy(alpha = 0.08f),
            strokeCap = StrokeCap.Round,
        )
        Spacer(Modifier.height(12.dp))
        LaunchButton(
            enabled = state.isComplete,
            onClick = onLaunch,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun LaunchButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.splash_enter_home),
        modifier = modifier
            .border(
                width = 1.dp,
                color = SignalCyan.copy(alpha = if (enabled) 0.48f else 0.15f),
                shape = RoundedCornerShape(50),
            )
            .background(
                color = if (enabled) SignalCyan else SignalCyan.copy(alpha = 0.03f),
                shape = RoundedCornerShape(50),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        color = if (enabled) SpaceBlack else MutedText.copy(alpha = 0.55f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp,
    )
}

@Composable
private fun DnsNodeCard(node: DnsNodeState) {
    val accent by animateColorAsState(
        targetValue = when (node.status) {
            DnsNodeStatus.Queued -> MutedText
            DnsNodeStatus.Resolving -> SignalBlue
            DnsNodeStatus.Resolved -> SignalCyan
            DnsNodeStatus.Fallback -> SignalAmber
        },
        animationSpec = tween(350),
        label = "node-accent",
    )
    val statusText = when (node.status) {
        DnsNodeStatus.Queued -> stringResource(Res.string.splash_node_queued)
        DnsNodeStatus.Resolving -> stringResource(Res.string.splash_node_resolving)
        DnsNodeStatus.Resolved -> stringResource(Res.string.splash_node_resolved)
        DnsNodeStatus.Fallback -> stringResource(Res.string.splash_node_safe_cache)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = 0.32f), RoundedCornerShape(12.dp))
            .background(Color(0xB20A171B), RoundedCornerShape(12.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .border(1.dp, accent, CircleShape)
                .padding(2.dp)
                .background(accent.copy(alpha = if (node.status == DnsNodeStatus.Queued) 0.2f else 1f), CircleShape)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.hostname,
                color = Color.White.copy(alpha = 0.94f),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = node.addresses.joinToString("  ·  ").ifBlank {
                    stringResource(Res.string.splash_address_pending)
                },
                color = MutedText,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = statusText,
            color = accent,
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
        )
    }
}

@Composable
private fun TechGrid(scanProgress: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val grid = 34.dp.toPx()
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = SignalCyan.copy(alpha = 0.035f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
            x += grid
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = SignalCyan.copy(alpha = 0.035f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += grid
        }

        val scanY = size.height * scanProgress
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, SignalCyan.copy(alpha = 0.12f), Color.Transparent),
                startY = scanY - 70.dp.toPx(),
                endY = scanY + 70.dp.toPx(),
            ),
            topLeft = Offset(0f, scanY - 70.dp.toPx()),
            size = Size(size.width, 140.dp.toPx()),
        )
        drawLine(
            color = SignalCyan.copy(alpha = 0.3f),
            start = Offset(0f, scanY),
            end = Offset(size.width, scanY),
            strokeWidth = 1.dp.toPx(),
        )
    }
}
