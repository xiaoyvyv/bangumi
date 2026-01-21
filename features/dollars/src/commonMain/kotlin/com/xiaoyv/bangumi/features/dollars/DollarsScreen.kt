package com.xiaoyv.bangumi.features.dollars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.reply_comment_send
import com.xiaoyv.bangumi.core_resource.resources.reply_dollars_hint
import com.xiaoyv.bangumi.core_resource.resources.type_feature_dollars
import com.xiaoyv.bangumi.features.dollars.business.DollarsEvent
import com.xiaoyv.bangumi.features.dollars.business.DollarsState
import com.xiaoyv.bangumi.features.dollars.business.DollarsViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun DollarsRoute(
    viewModel: DollarsViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    DollarsScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is DollarsEvent.UI.OnNavUp -> onNavUp()
                is DollarsEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun DollarsScreen(
    baseState: BaseState<DollarsState>,
    onUiEvent: (DollarsEvent.UI) -> Unit,
    onActionEvent: (DollarsEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.type_feature_dollars),
                onNavigationClick = { onUiEvent(DollarsEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(DollarsEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            DollarsScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun DollarsScreenContent(
    state: DollarsState,
    onUiEvent: (DollarsEvent.UI) -> Unit,
    onActionEvent: (DollarsEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .imePadding()
    ) {
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = state.items.lastIndex.coerceAtLeast(0)
        )
        val density = LocalDensity.current
        val imeVisible = WindowInsets.ime.getBottom(density) > 0

        LaunchedEffect(imeVisible, state.items.size) {
            delay(300)
            if (imeVisible && state.items.isNotEmpty()) {
                listState.scrollToItem(state.items.lastIndex)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = listState
        ) {
            items(state.items) {
                DollarsScreenContentItem(it, onUiEvent, onActionEvent)
            }
        }

        BgmHorizontalDivider()

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(LayoutPaddingHalf)
                .background(MaterialTheme.colorScheme.surfaceBright, MaterialTheme.shapes.small),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        ) {
            BmgTextField(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(LayoutPaddingHalf),
                value = state.value,
                onValueChange = { onActionEvent(DollarsEvent.Action.OnValueChange(it)) },
                shape = MaterialTheme.shapes.small,
                maxLines = 6,
                minLines = 3,
                placeholder = { Text(text = stringResource(Res.string.reply_dollars_hint)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
            )

            Button(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = LayoutPaddingHalf, end = LayoutPaddingHalf)
                    .resetSize(),
                enabled = state.value.text.isNotBlank() && state.sending != LoadingState.Loading,
                onClick = { onActionEvent(DollarsEvent.Action.OnSendMessage) },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier.alpha(if (state.sending == LoadingState.Loading) 0f else 1f),
                        text = stringResource(Res.string.reply_comment_send),
                        style = MaterialTheme.typography.labelLarge
                    )

                    if (state.sending == LoadingState.Loading) CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}


@Composable
private fun DollarsScreenContentItem(
    item: ComposeDollarItem,
    onUiEvent: (DollarsEvent.UI) -> Unit,
    onActionEvent: (DollarsEvent.Action) -> Unit,
) {
    val user = currentUser()
    val isSelf = user.id == item.uid

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LayoutPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp, if (isSelf) Alignment.End else Alignment.Start)
    ) {
        if (isSelf) {
            BgmLinkedText(
                modifier = Modifier
                    .weight(1f, false)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp),
                text = item.contentHtml,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
            )

            StateImage(
                modifier = Modifier.size(44.dp),
                shape = MaterialTheme.shapes.small,
                model = item.avatar,
            )
        } else {
            StateImage(
                modifier = Modifier.size(44.dp),
                shape = MaterialTheme.shapes.small,
                model = item.avatar,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                Text(
                    text = item.nickname,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                BgmLinkedText(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .border(CardDefaults.outlinedCardBorder(true), MaterialTheme.shapes.small)
                        .padding(12.dp),
                    text = item.contentHtml,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

