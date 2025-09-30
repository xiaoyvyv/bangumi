package com.xiaoyv.bangumi.shared.ui.component.layout.state

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_refresh
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.PullToRefreshBox
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun rememberCacheWindowLazyListState(
    @FloatRange(from = 0.0) aheadFraction: Float = 10f,
    @FloatRange(from = 0.0) behindFraction: Float = 10f,
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
    baseState: BaseState<T>,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    enablePullRefresh: Boolean = false,
    onRefresh: (Boolean) -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.(T) -> Unit,
) {
    if (enablePullRefresh) {
        var isRefreshing by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(baseState) {
            when (baseState) {
                is BaseState.Error<*> -> isRefreshing = false
                is BaseState.Loading<*> -> {}
                is BaseState.Success<*> -> isRefreshing = false
            }
        }
        PullToRefreshBox(
            modifier = modifier,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                onRefresh(false)
            },
            enabled = LocalCollapsingPullRefresh.current
        ) {
            StateLayoutImpl(
                modifier = Modifier.fillMaxSize(),
                baseState = baseState,
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
            baseState = baseState,
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
    baseState: BaseState<T>,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    onRefresh: (Boolean) -> Unit = {},
    content: @Composable BoxScope.(T) -> Unit,
) {
    when (baseState) {
        is BaseState.Loading -> StateLoadingLayout()
        is BaseState.Error -> StateErrorLayout(
            throwable = baseState.error ?: IllegalStateException(),
            onRefresh = onRefresh
        )

        is BaseState.Success -> Box(
            modifier = Modifier
                .background(containerColor)
                .then(modifier),
            contentAlignment = contentAlignment,
            propagateMinConstraints = propagateMinConstraints
        ) {
            content(baseState.data)
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
    throwable: Throwable,
    bias: Float = 0.4f,
    onRefresh: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .heightIn(min = 400.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val clampedBias = bias.coerceIn(0f, 1f)

        Spacer(modifier = Modifier.weight(clampedBias))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPadding * 2),
            text = throwable.errMsg,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onRefresh(true) }) {
            Text(text = stringResource(Res.string.global_refresh))
        }

        Spacer(modifier = Modifier.weight(1f - clampedBias))
    }
}