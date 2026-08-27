package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_message
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_title
import com.xiaoyv.bangumi.core_resource.resources.global_character
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_detail
import com.xiaoyv.bangumi.core_resource.resources.global_episode
import com.xiaoyv.bangumi.core_resource.resources.global_no_summary
import com.xiaoyv.bangumi.core_resource.resources.global_parade
import com.xiaoyv.bangumi.core_resource.resources.global_preview
import com.xiaoyv.bangumi.core_resource.resources.global_related_index
import com.xiaoyv.bangumi.core_resource.resources.global_related_subject
import com.xiaoyv.bangumi.core_resource.resources.global_score
import com.xiaoyv.bangumi.core_resource.resources.global_summary
import com.xiaoyv.bangumi.core_resource.resources.global_tag
import com.xiaoyv.bangumi.core_resource.resources.subject_action_deleted
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.core_resource.resources.subject_action_score
import com.xiaoyv.bangumi.core_resource.resources.subject_standard_deviation
import com.xiaoyv.bangumi.core_resource.resources.subject_standard_deviation_tip
import com.xiaoyv.bangumi.core_resource.resources.tracking_chap_all_completed
import com.xiaoyv.bangumi.core_resource.resources.tracking_ep_all_watched
import com.xiaoyv.bangumi.core_resource.resources.tracking_vol_all_read
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectDetailTab
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.button.collectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.chart.RatingBarChart
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.box.MaxHeightFadeBox
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalHalfBlackToTransparent
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfBlack
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutGridWidth
import com.xiaoyv.bangumi.shared.ui.component.tab.DetailSectionTitle
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.kts.isMediumScreen
import com.xiaoyv.bangumi.shared.ui.kts.isSmallScreen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodePager
import com.xiaoyv.bangumi.shared.ui.view.index.IndexCardItem
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectCollectionDialog
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectTagColumn
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectTrackingBar
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * [SubjectDetailMainScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailMainScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // 收藏
        SubjectDetailCollection(state, onUiEvent, onActionEvent)

        // 章节
        if (state.subject.type == SubjectType.MUSIC
            || state.subject.type == SubjectType.REAL
            || state.subject.type == SubjectType.ANIME
        ) {
            SubjectDetailEpisode(state, onUiEvent, onActionEvent)
        }

        // 描述
        SubjectDetailSummary(state, onUiEvent, onActionEvent)
        // 标签
        SubjectDetailTag(state, onUiEvent, onActionEvent)

        // 图片预览 | 巡礼数据
        if (state.subject.type == SubjectType.REAL || state.subject.type == SubjectType.ANIME) {
            // 图片预览
            if (state.photo.photos.isNotEmpty()) SubjectDetailPreview(state, onUiEvent, onActionEvent)
            // 巡礼数据
            if (state.parade.litePoints.isNotEmpty()) SubjectDetailParade(state, onUiEvent, onActionEvent)
        }

        // 详细信息
        SubjectDetailInfo(state, onUiEvent, onActionEvent)
        // 评分
        SubjectDetailScore(state, onUiEvent, onActionEvent)
        // 相关人物
        SubjectDetailCharacter(state, onUiEvent, onActionEvent)
        // 相关条目
        SubjectDetailRelated(state, onUiEvent, onActionEvent)
        // 相关目录
        SubjectDetailIndexList(state, onUiEvent, onActionEvent)
    }
}


@Composable
private fun SubjectDetailCollection(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    val deleteConfirmDialogState = rememberAlertDialogState()
    val collectionDialogState = rememberSheetDialogState()

    BgmAlertDialog(
        state = deleteConfirmDialogState,
        title = stringResource(Res.string.collect_cancel_title),
        text = stringResource(Res.string.collect_cancel_message),
        onConfirm = {
            deleteConfirmDialogState.dismiss()
            onActionEvent(SubjectDetailEvent.Action.DeleteCollection)
        }
    )

    SubjectCollectionDialog(
        state = collectionDialogState,
        loadState = state.loading,
        subject = state.subject,
        myTags = state.myTags,
        onCollectionUpdate = {
            onActionEvent(SubjectDetailEvent.Action.OnUpdateSubjectCollection(it, showLoadingDialog = false))
        }
    )

    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_collection),
        action = if (state.subject.interest.type == CollectionType.UNKNOWN) null else stringResource(Res.string.subject_action_deleted),
        onActionClick = { deleteConfirmDialogState.show() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin),
            verticalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            val sharedState = LocalSharedState.current

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = collectionButtonColors(state.subject.interest.type),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    if (sharedState.isLogin) collectionDialogState.show() else {
                        onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.SignIn))
                    }
                },
            ) {
                Text(text = state.subject.rememberDisplayMyCollectionText())
            }

            Text(
                text = state.subject.rememberDisplayCollectionInfo(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
private fun SubjectDetailEpisode(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin)
            .animateContentSize(),
        title = stringResource(Res.string.global_episode),
        onActionClick = {}
    ) {
        val subject = state.subject
        if (subject.interest.type != CollectionType.UNKNOWN) {
            val doneActionText = CollectionType.string(subject.type, CollectionType.DONE)
            when (subject.type) {
                // 动画和电视剧
                SubjectType.ANIME, SubjectType.REAL -> {
                    val firstUnCollectEp = remember(subject.episodes) {
                        subject.episodes.firstOrNull { it.collection.status == CollectionEpisodeType.UNKNOWN }
                    }

                    SubjectTrackingBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ContentMargin),
                        status = subject.interest.epStatus,
                        total = subject.eps,
                        button = buildString {
                            if (firstUnCollectEp == null) append(stringResource(Res.string.tracking_ep_all_watched)) else {
                                append("Ep.")
                                append(firstUnCollectEp.sortOrder.toTrimString())
                                append(doneActionText)
                            }
                        },
                        onClickIncrease = {
                            // 进度加一
                            if (firstUnCollectEp != null) {
                                onActionEvent(
                                    SubjectDetailEvent.Action.OnUpdateEpisodeCollection(
                                        episodes = listOf(firstUnCollectEp),
                                        type = CollectionEpisodeType.DONE
                                    )
                                )
                            }
                        }
                    )
                }
                // 书籍
                SubjectType.BOOK -> {
                    val nextEp = subject.interest.epStatus + 1
                    val nextVol = subject.interest.volStatus + 1

                    SubjectTrackingBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ContentMargin),
                        status = subject.interest.epStatus,
                        total = subject.eps,
                        button = buildString {
                            when {
                                subject.eps == 0 -> append("Chap.$nextEp$doneActionText")
                                nextEp >= subject.eps -> append(stringResource(Res.string.tracking_chap_all_completed))
                                else -> append("Chap.$nextEp$doneActionText")
                            }
                        },
                        onInputChangeConfirm = {
                            // 直接更新进度值
                            onActionEvent(
                                SubjectDetailEvent.Action.OnUpdateSubjectProgress(
                                    update = CollectionSubjectProgressParam(epStatus = it),
                                    showLoadingDialog = true
                                )
                            )
                        },
                        onClickIncrease = {
                            // 直接更新进度值
                            onActionEvent(
                                SubjectDetailEvent.Action.OnUpdateSubjectProgress(
                                    update = CollectionSubjectProgressParam(epStatus = nextEp),
                                    showLoadingDialog = true
                                )
                            )
                        }
                    )

                    SubjectTrackingBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ContentMargin),
                        status = subject.interest.volStatus,
                        total = subject.volumes,
                        button = buildString {
                            when {
                                subject.volumes == 0 -> append("Vol.$nextVol$doneActionText")
                                nextVol >= subject.volumes -> append(stringResource(Res.string.tracking_vol_all_read))
                                else -> append("Vol.$nextVol$doneActionText")
                            }
                        },
                        onInputChangeConfirm = {
                            // 直接更新进度值
                            onActionEvent(
                                SubjectDetailEvent.Action.OnUpdateSubjectProgress(
                                    update = CollectionSubjectProgressParam(volStatus = it),
                                    showLoadingDialog = true
                                )
                            )
                        },
                        onClickIncrease = {
                            // 直接更新进度值
                            onActionEvent(
                                SubjectDetailEvent.Action.OnUpdateSubjectProgress(
                                    update = CollectionSubjectProgressParam(volStatus = nextVol),
                                    showLoadingDialog = true
                                )
                            )
                        }
                    )
                }
            }
        }

        EpisodePager(
            modifier = Modifier.fillMaxWidth(),
            episodes = state.subject.episodes,
            onEpisodeChange = { eps, type ->
                onActionEvent(SubjectDetailEvent.Action.OnUpdateEpisodeCollection(eps, type))
            },
            onClickEpisode = {
                onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.TopicDetail(it.id, TopicType.TYPE_EP)))
            }
        )
    }
}

@Composable
private fun SubjectDetailSummary(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_summary),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.subject.summary))) }
    ) {
        MaxHeightFadeBox(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.subject.summary))) },
            maxHeight = 300.dp,
        ) {
            BgmLinkedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin),
                text = state.subject.summary.ifBlank { stringResource(Res.string.global_no_summary) },
            )
        }
    }
}

@Composable
private fun SubjectDetailTag(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_tag),
        onActionClick = {}
    ) {
        SubjectTagColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin),
            subject = state.subject,
            onClick = {
                onUiEvent(
                    SubjectDetailEvent.UI.OnNavScreen(
                        Screen.SubjectBrowser(
                            body = SubjectBrowserBody(
                                subjectType = SubjectType.ANIME,
                                sort = SubjectSortBrowserType.TRENDS,
                                tags = persistentListOf(it.name)
                            )
                        )
                    )
                )
            }
        )
    }
}

@Composable
private fun SubjectDetailPreview(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_preview),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(SubjectDetailEvent.UI.OnSelectedPageType(SubjectDetailTab.INDEX)) }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = ContentMargin),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            itemsIndexed(state.photo.photos) { index, item ->
                StateImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(item.displayNormalImage.displayAspect)
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onUiEvent(
                                SubjectDetailEvent.UI.OnNavScreen(
                                    Screen.PreviewMain(
                                        index,
                                        state.photo.photos.map { it.displayLargeImage.url.orEmpty() }
                                    )
                                )
                            )
                        },
                    model = item.displayNormalImage.url,
                    shape = MaterialTheme.shapes.small,
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailParade(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_parade),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = {
            onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Web(state.subject.paradeUrl)))
        }
    ) {
        val photos = state.parade.litePoints.orEmpty()

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = ContentMargin),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            items(photos) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(16 / 9f)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.Web(state.subject.paradeUrl))) }
                ) {
                    StateImage(
                        modifier = Modifier.matchParentSize(),
                        model = item.image?.substringBefore("?"),
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrushVerticalHalfBlackToTransparent)
                            .padding(horizontal = ContentMarginHalf)
                            .padding(top = ContentMarginHalf, bottom = ContentMarginHalf * 2),
                        text = item.displayEp,
                        textAlign = TextAlign.End,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrushVerticalTransparentToHalfBlack)
                            .padding(horizontal = ContentMarginHalf)
                            .padding(bottom = ContentMarginHalf, top = ContentMarginHalf * 2)
                            .align(Alignment.BottomCenter),
                        text = item.cn.orEmpty().ifBlank { item.name.orEmpty() },
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectDetailInfo(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_detail),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.subject.webInfo.info))) }
    ) {
        MaxHeightFadeBox(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.subject.webInfo.info))) },
            maxHeight = 300.dp,
        ) {
            BgmLinkedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin),
                text = state.subject.webInfo.shortInfo,
            )
        }
    }
}

@Composable
private fun SubjectDetailScore(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_score),
        action = stringResource(Res.string.subject_action_score),
        onActionClick = {
            onUiEvent(SubjectDetailEvent.UI.OnSelectedPageType(SubjectDetailTab.CHART))
        }
    ) {
        val standardDeviation = state.subject.rememberDisplayStandardDeviation()
        if (standardDeviation > 0) {
            val tooltipState = rememberTooltipState(isPersistent = true)
            TooltipBox(
                modifier = Modifier.padding(horizontal = ContentMargin),
                state = tooltipState,
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below, 4.dp),
                tooltip = {
                    PlainTooltip(
                        modifier = Modifier.padding(horizontal = ContentMargin),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            modifier = Modifier.padding(ContentMarginHalf),
                            text = stringResource(Res.string.subject_standard_deviation_tip),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            ) {
                val scope = rememberCoroutineScope()
                Text(
                    modifier = Modifier.clickWithoutRipped {
                        scope.launch {
                            if (tooltipState.isVisible) tooltipState.dismiss() else tooltipState.show()
                        }
                    },
                    textDecoration = TextDecoration.Underline,
                    text = stringResource(
                        Res.string.subject_standard_deviation,
                        standardDeviation.toTrimString()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        RatingBarChart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin)
                .padding(top = ContentMargin)
                .height(200.dp),
            rating = state.subject.displayRateCountMap
        )
    }
}

@Composable
@OptIn(ExperimentalGridApi::class)
private fun SubjectDetailCharacter(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_character),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(SubjectDetailEvent.UI.OnSelectedPageType(SubjectDetailTab.CHARACTER)) }
    ) {
        val column = if (isSmallScreen) 3 else if (isMediumScreen) 5 else 7
        val row = if (isSmallScreen) 3 else if (isMediumScreen) 2 else 1

        val gridSpacing = ContentMargin
        Grid(
            config = {
                repeat(column) { column(minmax(0.dp, 1.fr)) }
                gap(gridSpacing)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin),
        ) {
            val items = remember(column, state.characters) {
                state.characters.take(column * row)
            }

            items.fastForEach {
                MonoCardItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = it,
                    onClick = { id, type ->
                        onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.MonoDetail(id, type)))
                    }
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailRelated(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_related_subject),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = {
            onUiEvent(SubjectDetailEvent.UI.OnSelectedPageType(SubjectDetailTab.RELATED))
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            contentPadding = PaddingValues(horizontal = ContentMargin),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            items(state.related, key = { it.subject.id }, contentType = { "Subject" }) {
                SubjectCardItem(
                    modifier = Modifier.width(LayoutGridWidth),
                    display = it,
                    maxLine = 1,
                    onClick = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) },
                )
            }
        }
    }
}


@Composable
private fun SubjectDetailIndexList(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_related_index),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = {
            onUiEvent(SubjectDetailEvent.UI.OnSelectedPageType(SubjectDetailTab.INDEX))
        }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            contentPadding = PaddingValues(horizontal = ContentMargin),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
        ) {
            items(
                state.subject.webInfo.indexList,
                key = { it.id },
                contentType = { "Index" }
            ) {
                IndexCardItem(
                    modifier = Modifier
                        .width(180.dp)
                        .fillParentMaxHeight(),
                    item = it,
                    onClick = {
                        onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.IndexDetail(it.id)))
                    }
                )
            }
        }
    }
}
