package com.xiaoyv.bangumi.shared.ui.component.layout.state

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_empty_comments_subtitle
import com.xiaoyv.bangumi.core_resource.resources.global_empty_comments_title
import com.xiaoyv.bangumi.core_resource.resources.global_no_more
import com.xiaoyv.bangumi.core_resource.resources.global_no_more_comments_subtitle
import com.xiaoyv.bangumi.core_resource.resources.global_refresh
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmRequireLoginLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.PullToRefreshBox
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource

@Composable
fun rememberCacheWindowLazyListState(
    @FloatRange(from = 0.0) aheadFraction: Float = 1f,
    @FloatRange(from = 0.0) behindFraction: Float = 1f,
): LazyListState {
    return rememberLazyListState()
}

@Composable
fun rememberCacheWindowLazyGridState(
    @FloatRange(from = 0.0) aheadFraction: Float = 10f,
    @FloatRange(from = 0.0) behindFraction: Float = 10f,
): LazyGridState {
    return rememberLazyGridState()
}

/**
 * The state layout
 *
 * [StateLayout]
 */
@Composable
fun <T> StateLayout(
    modifier: Modifier = Modifier,
    uiState: UiState<T>,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    enablePullRefresh: Boolean = false,
    onRefresh: (Boolean) -> Unit = {},
    pullRefreshIndicatorPaddingTop: Dp = 0.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.(T) -> Unit,
) {
    if (enablePullRefresh) {
        var isRefreshing by rememberSaveable { mutableStateOf(false) }
        val pullRefreshState = rememberPullToRefreshState()

        LaunchedEffect(uiState) {
            if (uiState.status !is PageStatus.Loading) isRefreshing = false
        }
        PullToRefreshBox(
            modifier = modifier,
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                onRefresh(false)
            },
            enabled = LocalCollapsingPullRefresh.current,
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = pullRefreshIndicatorPaddingTop),
                    isRefreshing = isRefreshing,
                    state = pullRefreshState
                )
            }
        ) {
            StateLayoutImpl(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                containerColor = containerColor,
                contentAlignment = contentAlignment,
                propagateMinConstraints = propagateMinConstraints,
                onRefresh = onRefresh,
                content = content
            )
        }
    } else {
        StateLayoutImpl(
            modifier = modifier,
            uiState = uiState,
            containerColor = containerColor,
            contentAlignment = contentAlignment,
            propagateMinConstraints = propagateMinConstraints,
            onRefresh = onRefresh,
            content = content
        )
    }
}

@Composable
private fun <T> StateLayoutImpl(
    modifier: Modifier = Modifier,
    uiState: UiState<T>,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    onRefresh: (Boolean) -> Unit = {},
    content: @Composable BoxScope.(T) -> Unit,
) {
    when (val status = uiState.status) {
        PageStatus.Loading -> StateLoadingLayout()
        is PageStatus.Error -> StateErrorLayout(
            message = status.message,
            throwable = status.throwable,
            onRefresh = onRefresh
        )

        PageStatus.Idle -> Box(
            modifier = Modifier
                .background(containerColor)
                .then(modifier),
            contentAlignment = contentAlignment,
            propagateMinConstraints = propagateMinConstraints
        ) {
            content(uiState.data)
        }
    }
}

@Composable
fun StateLoadingLayout(bias: Float = 0.4f) {
    val clampedBias = bias.coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .heightIn(min = 400.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(clampedBias))
        BgmProgressIndicator()
        Spacer(modifier = Modifier.weight(1f - clampedBias))
    }
}


@Composable
fun StateEmptyLayout(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .heightIn(min = 400.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "暂无内容")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onRefresh) {
            Text(text = stringResource(Res.string.global_refresh))
        }
    }
}

@Composable
fun StateErrorLayout(
    message: String = "",
    throwable: Throwable? = null,
    bias: Float = 0.4f,
    onRefresh: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = 400.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val clampedBias = bias.coerceIn(0f, 1f)

        Spacer(modifier = Modifier.weight(clampedBias))

        if (throwable is ApiHttpException && throwable.code == 401) {
            BgmRequireLoginLayout(modifier = Modifier.fillMaxSize()) {
                onRefresh(true)
            }
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ContentMargin),
                text = message.ifBlank { throwable.errMsg },
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Button(onClick = { onRefresh(true) }) {
                Text(text = stringResource(Res.string.global_refresh))
            }
        }

        Spacer(modifier = Modifier.weight(1f - clampedBias))
    }
}

@Composable
fun PagingAppendErrorLayout(
    throwable: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onRetry)
            .padding(horizontal = ContentMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "${throwable.errMsg}，点击重试",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun  CommentNoDataTip(
    isEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    val title = stringResource(
        if (isEmpty) Res.string.global_empty_comments_title
        else Res.string.global_no_more
    )
    val subtitle = stringResource(
        if (isEmpty) Res.string.global_empty_comments_subtitle
        else Res.string.global_no_more_comments_subtitle
    )

    if (!isEmpty) BgmHorizontalDivider()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .let { if (isEmpty) it.height(400.dp) else it.padding(bottom = 200.dp) }
            .padding(horizontal = ContentMargin),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf, Alignment.CenterVertically)
    ) {
        if (isEmpty) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(0.82f)
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin, vertical = 24.dp),
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
