package com.xiaoyv.bangumi.features.search.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.core_resource.resources.global_clear
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.search_clear_history_confirm
import com.xiaoyv.bangumi.core_resource.resources.search_history
import com.xiaoyv.bangumi.features.search.input.business.SearchInputEvent
import com.xiaoyv.bangumi.features.search.input.business.SearchInputSideEffect
import com.xiaoyv.bangumi.features.search.input.business.SearchInputState
import com.xiaoyv.bangumi.features.search.input.business.SearchInputViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SearchInputRoute(
    viewModel: SearchInputViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    viewModel.collectBaseSideEffect {
        when (it) {
            is SearchInputSideEffect.OnSearchResult -> {
                keyboardController?.hide()
                onNavScreen(Screen.SearchResult(it.value))
            }
        }
    }

    SearchInputScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SearchInputEvent.UI.OnNavUp -> onNavUp()
                is SearchInputEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SearchInputScreen(
    uiState: UiState<SearchInputState>,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .height(TopAppBarDefaults.TopAppBarExpandedHeight)
            ) {
                uiState.data.run {
                    SearchInputBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        state = this,
                        onActionEvent = onActionEvent,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp),
                    onClick = { onUiEvent(SearchInputEvent.UI.OnNavUp) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(Res.string.global_back),
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp),
                    onClick = { onActionEvent(SearchInputEvent.Action.OnSearch) },
                ) {
                    Icon(
                        imageVector = BgmIcons.Search,
                        contentDescription = stringResource(Res.string.global_search),
                    )
                }
            }
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(SearchInputEvent.Action.OnRefresh(it)) },
            uiState = uiState,
        ) { state ->
            SearchInputScreenContent(state, onActionEvent)
        }
    }
}


@Composable
private fun SearchInputScreenContent(
    state: SearchInputState,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    when {
        state.suggestions.isNotEmpty() -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.suggestions) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onActionEvent(SearchInputEvent.Action.OnQueryChange(it.asTextFieldValue()))
                            onActionEvent(SearchInputEvent.Action.OnSearch)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = ContentMargin, vertical = 12.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        state.histories.isNotEmpty() && state.query.text.isBlank() -> SearchInputHistory(
            state = state,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun SearchInputBar(
    modifier: Modifier = Modifier,
    state: SearchInputState,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    BmgTextField(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 56.dp, vertical = 14.dp),
        value = state.query,
        onValueChange = { onActionEvent(SearchInputEvent.Action.OnQueryChange(it)) },
        shape = CircleShape,
        autoFocus = true,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onActionEvent(SearchInputEvent.Action.OnSearch) }),
        placeholder = { Text(text = stringResource(Res.string.global_search)) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}


@Composable
private fun SearchInputHistory(
    state: SearchInputState,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    val clearHistoryDialogState = rememberAlertDialogState()

    BgmAlertDialog(
        state = clearHistoryDialogState,
        text = stringResource(Res.string.search_clear_history_confirm),
        onConfirm = { onActionEvent(SearchInputEvent.Action.OnClearHistory) },
    )

    Column(modifier = Modifier.fillMaxSize()) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = ContentMargin),
                text = stringResource(Res.string.search_history),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { clearHistoryDialogState.show() }) {
                Icon(
                    imageVector = BgmIcons.Delete,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(Res.string.global_clear)
                )
            }
            Spacer(modifier = Modifier.width(ContentMargin - 12.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.histories, key = { it }) { keyword ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onActionEvent(SearchInputEvent.Action.OnQueryChange(keyword.asTextFieldValue()))
                                onActionEvent(SearchInputEvent.Action.OnSearch)
                            }
                            .padding(start = ContentMargin, top = 12.dp, bottom = 12.dp),
                        text = keyword,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                    )
                    IconButton(
                        onClick = {
                            onActionEvent(SearchInputEvent.Action.OnDeleteHistory(keyword))
                        },
                    ) {
                        Icon(
                            imageVector = BgmIcons.Close,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(Res.string.global_clear),
                        )
                    }
                    Spacer(modifier = Modifier.width(ContentMargin - 12.dp))
                }
            }
        }
    }
}

