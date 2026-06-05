package com.xiaoyv.bangumi.features.settings.main.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_current
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_hint_degraded
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_hint_down
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_hint_ok
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_hint_unknown
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_title
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_updated_at
import com.xiaoyv.bangumi.core_resource.resources.settings_bgm_status_view_detail
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.api.client.createHttpClient
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import io.ktor.client.call.body
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
private data class BangumiStatusMini(
    @SerialName("message") val message: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("updated_at") val updatedAt: Long = 0L,
)

private data class StatusDotInfo(
    val color: Color,
    val breathing: Boolean,
)

@Composable
fun BangumiStatusTopBarAction(
    modifier: Modifier = Modifier,
    iconSize: Dp = 22.dp,
) {
    val actionHandler = LocalActionHandler.current
    val networkConfig = currentSettings().network
    val client = remember(networkConfig) { createHttpClient(config = networkConfig, logLevel = LogLevel.NONE) }
    DisposableEffect(client) { onDispose { client.close() } }

    val titleText = stringResource(Res.string.settings_bgm_status_title)
    val currentStatusText = stringResource(Res.string.settings_bgm_status_current)
    val viewDetailText = stringResource(Res.string.settings_bgm_status_view_detail)
    val unknownText = stringResource(Res.string.global_unknown)

    var expanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf<BangumiStatusMini?>(null) }
    var requestFailed by remember { mutableStateOf(false) }

    suspend fun refresh() {
        if (loading) return
        loading = true
        requestFailed = false
        runCatching {
            client.get(WebConstant.URL_BGM_STATUS_API).body<BangumiStatusMini>()
        }.onSuccess {
            data = it
        }.onFailure {
            requestFailed = true
        }
        loading = false
    }

    LaunchedEffect(Unit) { refresh() }
    LaunchedEffect(expanded) { if (expanded) refresh() }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Box(modifier = Modifier.size(iconSize)) {
                val status = data?.status?.takeIf { it.isNotBlank() }
                val indicatorStatus = if (requestFailed) null else status
                val showIconOnly = requestFailed.not() && (indicatorStatus == null || indicatorStatus == "ok")

                if (showIconOnly) {
                    Icon(
                        modifier = Modifier.matchParentSize(),
                        imageVector = BgmIcons.NetworkCheck,
                        contentDescription = titleText,
                    )
                } else {
                    val dotInfo = statusDotInfo(indicatorStatus)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .breathing(dotInfo.breathing)
                            .background(dotInfo.color, CircleShape)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 360.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium,
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = currentStatusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val dotInfo = statusDotInfo(if (requestFailed) null else data?.status?.takeIf { it.isNotBlank() })
                            StatusBreathingDot(color = dotInfo.color, breathing = dotInfo.breathing)
                            Spacer(Modifier.width(6.dp))

                            TextButton(
                                onClick = {
                                    expanded = false
                                    actionHandler.openInBrowser(WebConstant.URL_BGM_STATUS_WEB)
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                colors = ButtonDefaults.textButtonColors(),
                            ) {
                                Text(text = viewDetailText)
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    imageVector = BgmIconsMirrored.KeyboardArrowRight,
                                    contentDescription = null,
                                )
                            }

                            if (loading && data == null) {
                                Spacer(Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }

                    val hintOk = stringResource(Res.string.settings_bgm_status_hint_ok)
                    val hintDegraded = stringResource(Res.string.settings_bgm_status_hint_degraded)
                    val hintDown = stringResource(Res.string.settings_bgm_status_hint_down)
                    val hintUnknown = stringResource(Res.string.settings_bgm_status_hint_unknown)

                    val activeStatus = if (requestFailed) null else data?.status?.takeIf { it.isNotBlank() }
                    val inactiveHintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    val okHintColor = Color(0xFF55CC55)
                    val degradedHintColor = Color(0xFFFF9800)
                    val downHintColor = MaterialTheme.colorScheme.error
                    val unknownActiveHintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)

                    val hintText = remember(
                        hintOk,
                        hintDegraded,
                        hintDown,
                        hintUnknown,
                        activeStatus,
                        inactiveHintColor,
                        okHintColor,
                        degradedHintColor,
                        downHintColor,
                        unknownActiveHintColor,
                    ) {
                        buildAnnotatedString {
                            fun appendLine(text: String, active: Boolean, activeColor: Color) {
                                withStyle(SpanStyle(color = if (active) activeColor else inactiveHintColor)) {
                                    append(text)
                                }
                                append('\n')
                            }

                            appendLine(
                                text = hintOk,
                                active = activeStatus == "ok",
                                activeColor = okHintColor,
                            )
                            appendLine(
                                text = hintDegraded,
                                active = activeStatus == "degraded",
                                activeColor = degradedHintColor,
                            )
                            appendLine(
                                text = hintDown,
                                active = activeStatus == "down",
                                activeColor = downHintColor,
                            )
                            withStyle(
                                SpanStyle(
                                    color = if (activeStatus == null || (activeStatus != "ok" && activeStatus != "degraded" && activeStatus != "down"))
                                        unknownActiveHintColor
                                    else inactiveHintColor
                                )
                            ) {
                                append(hintUnknown)
                            }
                        }
                    }

                    Text(
                        text = hintText,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                val updatedAtText = data?.updatedAt
                    ?.takeIf { it > 0 }
                    ?.let { (it * 1000L).formatDate("yyyy-MM-dd HH:mm") }
                    ?: unknownText

                Text(
                    text = stringResource(Res.string.settings_bgm_status_updated_at, updatedAtText),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatusBreathingDot(
    color: Color,
    breathing: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .breathing(breathing)
            .background(color, CircleShape)
    )
}

@Composable
private fun statusDotInfo(status: String?): StatusDotInfo {
    return when (status) {
        "ok" -> StatusDotInfo(color = Color(0xFF55CC55), breathing = true)
        "degraded" -> StatusDotInfo(color = Color(0xFFFF9800), breathing = true)
        "down" -> StatusDotInfo(color = MaterialTheme.colorScheme.error, breathing = true)
        else -> StatusDotInfo(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), breathing = true)
    }
}

@Composable
private fun Modifier.breathing(enabled: Boolean): Modifier {
    if (!enabled) return this
    val transition = rememberInfiniteTransition(label = "BangumiStatusBreathing")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )
    return this.graphicsLayer(
        alpha = alpha,
        scaleX = scale,
        scaleY = scale,
    )
}
