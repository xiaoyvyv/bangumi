package com.xiaoyv.bangumi.features.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.magnet_search_hint
import com.xiaoyv.bangumi.core_resource.resources.magnet_title
import com.xiaoyv.bangumi.core_resource.resources.mikan_search_rule
import com.xiaoyv.bangumi.core_resource.resources.mikan_search_tip
import com.xiaoyv.bangumi.features.garden.business.GardenEvent
import com.xiaoyv.bangumi.features.garden.business.GardenSideEffect
import com.xiaoyv.bangumi.features.garden.business.GardenState
import com.xiaoyv.bangumi.features.garden.business.GardenViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.view.magnet.MikanMagnetItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GardenRoute(
    viewModel: GardenViewModel = koinViewModel<GardenViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    val pagingItems = viewModel.magnet.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {
        when (it) {
            GardenSideEffect.OnRefresh -> pagingItems.refresh()
        }
    }

    GardenScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is GardenEvent.UI.OnNavUp -> onNavUp()
                is GardenEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun GardenScreen(
    baseState: BaseState<GardenState>,
    pagingItems: LazyPagingItems<ComposeMikanResource>,
    onUiEvent: (GardenEvent.UI) -> Unit,
    onActionEvent: (GardenEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.magnet_title),
                actions = {
                    val dialogState = rememberAlertDialogState()
                    BgmAlertDialog(
                        state = dialogState,
                        cancel = null,
                        title = stringResource(Res.string.mikan_search_rule),
                        text = stringResource(Res.string.mikan_search_tip)
                    )
                    IconButton(onClick = { dialogState.show() }) {
                        Icon(
                            imageVector = BgmIconsMirrored.Help,
                            contentDescription = stringResource(Res.string.global_image)
                        )
                    }
                },
                onNavigationClick = { onUiEvent(GardenEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(GardenEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            GardenScreenContent(state, pagingItems, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun GardenScreenContent(
    state: GardenState,
    pagingItems: LazyPagingItems<ComposeMikanResource>,
    onUiEvent: (GardenEvent.UI) -> Unit,
    onActionEvent: (GardenEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val keyboardController = LocalSoftwareKeyboardController.current

        BmgTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(horizontal = LayoutPadding, vertical = 12.dp),
            colors = textFieldTransparentColors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            value = state.query,
            shape = MaterialTheme.shapes.small,
            onValueChange = {
                onActionEvent(GardenEvent.Action.OnTextChanged(it))
            },
            placeholder = {
                Text(text = stringResource(Res.string.magnet_search_hint))
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions {
                keyboardController?.hide()
                onActionEvent(GardenEvent.Action.OnSearch)
            },
            singleLine = true,
            autoFocus = true,
            trailingIcon = {
                IconButton(
                    modifier = Modifier.padding(end = LayoutPaddingHalf),
                    onClick = {
                        keyboardController?.hide()
                        onActionEvent(GardenEvent.Action.OnSearch)
                    }
                ) {
                    Icon(
                        imageVector = BgmIcons.Search,
                        contentDescription = stringResource(Res.string.global_search)
                    )
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(bottom = LayoutPaddingHalf, top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            DropMenuChip(
                options = TabTokens.magnetGardenTypes,
                current = state.param.typeId,
                trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                onOptionClick = {
                    onActionEvent(GardenEvent.Action.OnChangeParamBody(state.param.copy(typeId = it.type)))
                }
            )

            DropMenuChip(
                options = TabTokens.magnetGardenTeams,
                current = state.param.teamId,
                trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                onOptionClick = {
                    onActionEvent(GardenEvent.Action.OnChangeParamBody(state.param.copy(teamId = it.type)))
                }
            )

            if (state.query.text.isNotBlank()) DropMenuChip(
                options = TabTokens.magnetGardenSorts,
                current = state.param.order,
                trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                onOptionClick = {
                    onActionEvent(GardenEvent.Action.OnChangeParamBody(state.param.copy(order = it.type)))
                }
            )
        }

        StateLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            pagingItems = pagingItems,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = LayoutPaddingHalf),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { item, index ->
            MikanMagnetItem(
                modifier = Modifier.fillMaxWidth(),
                item = item
            )
        }
    }
}

