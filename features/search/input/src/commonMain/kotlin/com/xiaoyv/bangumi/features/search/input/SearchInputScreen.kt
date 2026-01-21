package com.xiaoyv.bangumi.features.search.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.core_resource.resources.global_clear
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.search_history
import com.xiaoyv.bangumi.features.search.input.business.SearchInputEvent
import com.xiaoyv.bangumi.features.search.input.business.SearchInputSideEffect
import com.xiaoyv.bangumi.features.search.input.business.SearchInputState
import com.xiaoyv.bangumi.features.search.input.business.SearchInputViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
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
        baseState = baseState,
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
    baseState: BaseState<SearchInputState>,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = null,
                titleContent = {
                    baseState.content {
                        SearchInputBar(this, onUiEvent, onActionEvent)
                    }
                }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(SearchInputEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            SearchInputScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SearchInputScreenContent(
    state: SearchInputState,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    when {
        state.suggestions.isNotEmpty() -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(state.suggestions) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onActionEvent(SearchInputEvent.Action.OnQueryChange(it.asTextFieldValue()))
                            onActionEvent(SearchInputEvent.Action.OnSearch)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = contentMargin, vertical = 12.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        state.histories.isNotEmpty() && state.query.text.isBlank() -> SearchInputHistory(
            state = state,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun SearchInputBar(
    state: SearchInputState,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    BmgTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = contentMargin - 12.dp, end = contentMargin),
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
        leadingIcon = {
            IconButton(onClick = { onUiEvent(SearchInputEvent.UI.OnNavUp) }) {
                Icon(
                    imageVector = BgmIconsMirrored.ArrowBack,
                    contentDescription = stringResource(Res.string.global_back)
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        trailingIcon = {
            IconButton(onClick = { onActionEvent(SearchInputEvent.Action.OnSearch) }) {
                Icon(
                    imageVector = BgmIcons.Search,
                    contentDescription = stringResource(Res.string.global_search)
                )
            }
        }
    )
}


@Composable
private fun SearchInputHistory(
    state: SearchInputState,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = contentMargin),
                text = stringResource(Res.string.search_history),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onActionEvent(SearchInputEvent.Action.OnClearHistory) }) {
                Icon(
                    imageVector = BgmIcons.Delete,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = stringResource(Res.string.global_clear)
                )
            }
            Spacer(modifier = Modifier.width(contentMargin - 12.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.histories) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onActionEvent(SearchInputEvent.Action.OnQueryChange(it.asTextFieldValue()))
                            onActionEvent(SearchInputEvent.Action.OnSearch)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = contentMargin, vertical = 12.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
