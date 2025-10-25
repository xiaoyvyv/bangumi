package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_topic
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.grouped
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.tab.DetailSectionTitle
import com.xiaoyv.bangumi.shared.ui.theme.BgmDefaultIcons
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodeDropMenu
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodeItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * [SubjectDetailEpisodeScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailEpisodeScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    val items by produceState(persistentListOf(), state.subject.episodes) {
        value = state.subject.episodes.grouped().toPersistentList()
    }

    val listState = rememberLazyListState()
    val totalCount = items.size
    val showSlider = totalCount > 40

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (showSlider) 120.dp else 0.dp)
        ) {
            items(items) { episode ->
                if (!episode.splitter.isNullOrBlank()) {
                    DetailSectionTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = LayoutPadding),
                        title = episode.splitter.orEmpty(),
                        divider = true
                    )
                }
                if (episode.id != 0L) {
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        EpisodeItem(
                            modifier = Modifier.fillMaxWidth(),
                            subjectType = state.subject.type,
                            item = episode,
                            contentPadding = PaddingValues(horizontal = LayoutPadding, vertical = 12.dp),
                            onClick = { expanded = true }
                        )

                        EpisodeDropMenu(
                            episodes = items,
                            item = episode,
                            offset = DpOffset(x = 120.dp, y = 0.dp),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            onEpisodeChange = { eps, type ->
                                onActionEvent(SubjectDetailEvent.Action.OnUpdateEpisodeCollection(eps, type))
                            },
                            onClickEpisode = {
                                onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Article(episode.id, RakuenIdType.TYPE_EP)))
                            },
                            content = {
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Garden(state.magnetQuery(episode))))
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = BgmDefaultIcons.Explore,
                                            contentDescription = stringResource(Res.string.global_topic)
                                        )
                                    },
                                    text = {
                                        Text(text = "资源检索")
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        if (showSlider) {
            EpisodeBottomSlider(listState, totalCount)
        }
    }
}

@Composable
private fun BoxScope.EpisodeBottomSlider(
    listState: LazyListState,
    totalCount: Int,
) {
    // 是否正在拖动 Slider
    var isUserDragging by remember { mutableStateOf(false) }
    // Slider 的临时值，直接用 index
    var sliderValue by remember { mutableStateOf(0) }

    // 计算列表进度，直接对应 index
    val listIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex.coerceIn(0, totalCount - 1)
        }
    }

    // 当没有在拖动时，才同步列表进度到 Slider
    LaunchedEffect(listIndex, isUserDragging) {
        if (!isUserDragging) {
            sliderValue = listIndex
        }
    }

    Card(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(0.9f)
            .padding(bottom = 40.dp),
        shape = RoundedCornerShape(50),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val coroutineScope = rememberCoroutineScope()

        Slider(
            modifier = Modifier.padding(horizontal = LayoutPadding),
            value = sliderValue.toFloat(),
            onValueChange = { newValue ->
                isUserDragging = true
                val targetIndex = newValue.toInt().coerceIn(0, totalCount - 1)
                sliderValue = targetIndex
                if (listState.firstVisibleItemIndex != targetIndex) {
                    coroutineScope.launch { listState.scrollToItem(targetIndex) }
                }
            },
            onValueChangeFinished = { isUserDragging = false },
            valueRange = 0f..(totalCount - 1).toFloat(),
            steps = totalCount - 2,
        )
    }
}


