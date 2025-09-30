package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinMonoDetailCastsViewModel(param: ListPersonCastParam): MonoDetailCastsViewModel {
    return koinViewModel(
        key = param.toString(),
        parameters = { parametersOf(param) }
    )
}

class MonoDetailCastsViewModel(param: ListPersonCastParam, monoRepository: MonoRepository) : ViewModel() {
    private val castPager = monoRepository.fetchPersonCastPager(param)
    val casts = castPager.flow.cachedIn(viewModelScope)
}

/**
 * [MonoDetailWorksScreen]
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailCastsScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel = koinMonoDetailCastsViewModel(param = remember {
        ListPersonCastParam(
            personId = state.id,
        )
    })

    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = viewModel.casts.collectAsLazyPagingItems()
    ) { item, index ->
        MonoCastItem(item, state, onUiEvent)
    }
}


@Composable
fun MonoCastItem(
    item: ComposeMonoInfo,
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
) {
    if (state.type == MonoType.CHARACTER) {
        MonoCharacterCastItem(item, onUiEvent)
    } else {
        MonoPersonCastItem(item, onUiEvent)
    }
    BgmHorizontalDivider()
}

@Composable
private fun MonoCharacterCastItem(
    item: ComposeMonoInfo,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id))) }
            .fillMaxWidth()
            .padding(LayoutPadding),
        horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            // 角色出演
            InfoImage(
                modifier = Modifier.width(60.dp),
                blur = item.subject.images.displayGridImage,
                model = item.subject.images.displayLargeImage,
                maskText = stringResource(SubjectType.string(item.subject.type)),
                textStyle = MaterialTheme.typography.bodySmall,
                onClick = {
                    onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id)))
                }
            )

            Column(Modifier.weight(1f), Arrangement.spacedBy(LayoutPaddingHalf)) {
                Text(
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = item.subject.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    text = MonoCastType.string(item.type),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (item.actors.isNotEmpty()) Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            // 角色出演对应的CV
            item.actors.forEach {
                Row(horizontalArrangement = Arrangement.spacedBy(LayoutPadding)) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            modifier = Modifier,
                            text = it.displayName,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "CV",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    InfoImage(
                        modifier = Modifier.width(60.dp),
                        model = it.images.displayLargeImage,
                        blur = it.images.displayGridImage,
                        onClick = {
                            onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, MonoType.PERSON)))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonoPersonCastItem(
    item: ComposeMonoInfo,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.MonoDetail(item.mono.id, MonoType.CHARACTER))) }
            .fillMaxWidth()
            .padding(LayoutPadding),
        horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            // 人物出演
            InfoImage(
                modifier = Modifier.width(60.dp),
                blur = item.mono.images.displayGridImage,
                model = item.mono.images.displayLargeImage,
                maskText = item.mono.name,
                textStyle = MaterialTheme.typography.bodySmall,
                onClick = {
                    onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.MonoDetail(item.mono.id, MonoType.CHARACTER)))
                }
            )

            Column(Modifier.weight(1f), Arrangement.spacedBy(4.dp)) {
                Text(
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = item.mono.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = item.mono.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            // 人物出演对应的条目
            item.relations.forEach {
                Row(horizontalArrangement = Arrangement.spacedBy(LayoutPadding)) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            modifier = Modifier,
                            text = it.subject.displayName,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            text = MonoCastType.string(it.type),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    InfoImage(
                        modifier = Modifier.width(60.dp),
                        model = it.subject.images.displayLargeImage,
                        blur = it.subject.images.displayGridImage,
                        onClick = {
                            onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id)))
                        }
                    )
                }
            }
        }
    }
}