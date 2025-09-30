package com.xiaoyv.bangumi.features.main.tab.tracking.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_rank_no
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingEvent
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.manager.app.fromPersonState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeSubjectLazyItems
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.episode.EpisodePager
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectTrackingBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_SUBJECT_ITEM = "CONTENT_TYPE_SUBJECT_ITEM"

@Composable
fun TrackingPageScreen(
    @SubjectType subjectType: Int,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    viewModel: TrackingPageViewModel = koinTrackingPageViewModel(subjectType),
) {
    val pagingItems = viewModel.collections.collectAsLazyPagingItems()
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect { }

    baseState.content {
        TrackingPageScreenContent(
            state = this,
            pagingItems = pagingItems,
            onUiEvent = onUiEvent,
            onActionEvent = { viewModel.onEvent(it) }
        )
    }
}

@Composable
private fun TrackingPageScreenContent(
    state: TrackingPageState,
    pagingItems: LazyPagingItems<ComposeSubject>,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    onActionEvent: (TrackingPageEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberCacheWindowLazyListState(),
        pagingItems = pagingItems,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_SUBJECT_ITEM }
    ) { item, index ->
        val item = item.fromPersonState()

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPaddingHalf)
                .animateContentSize()
        ) {
            TrackingPageScreenContentItem(
                state = state,
                item = item,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            if (state.showEp && item.episodes.isNotEmpty()) {
                EpisodePager(
                    episodes = item.episodes,
                    maxRows = currentSettings().ui.trackingGridLineLimit,
                    onEpisodeChange = { epIds, type ->
                        onActionEvent(TrackingPageEvent.Action.OnUpdateEpisodeCollection(item, epIds, type))
                    },
                    onClickEpisode = {
                        onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.Article(it.id, RakuenIdType.TYPE_EP)))
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackingPageScreenContentItem(
    state: TrackingPageState,
    item: ComposeSubject,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    onActionEvent: (TrackingPageEvent.Action) -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SubjectDetail(item.id))) }
            .padding(top = LayoutPaddingHalf),
        leadingContent = {
            Box(
                modifier = Modifier
                    .width(85.dp)
                    .aspectRatio(3 / 4f)
            ) {
                StateImage(
                    modifier = Modifier.matchParentSize(),
                    shape = MaterialTheme.shapes.small,
                    model = item.images.displayLargeImage,
                )

                if (item.rating.rank != 0) Text(
                    modifier = Modifier
                        .padding(top = LayoutPaddingHalf)
                        .align(Alignment.TopStart)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                        )
                        .padding(LayoutPaddingHalf, 4.dp),
                    text = stringResource(Res.string.global_rank_no) + " " + item.rating.rank,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        overlineContent = {
            Text(
                text = buildAnnotatedString {
                    append(item.displayName)
                    if (item.rating.score > 0) {
                        append(" ")
                        withStyle(SpanStyle(color = StarColor, fontWeight = FontWeight.Medium)) {
                            append(item.rating.score.toString())
                        }
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LayoutPaddingHalf / 2),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf / 2)
            ) {
                Text(
                    text = "${item.collection.doing}人在看",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                val doneActionText = CollectionType.string(item.type, CollectionType.DONE)

                if (item.type == SubjectType.ANIME || item.type == SubjectType.REAL) {
                    val firstUnCollectEp = remember(item.episodes) {
                        item.episodes.firstOrNull { it.collection.status == CollectionEpisodeType.UNKNOWN }
                    }

                    SubjectTrackingBar(
                        modifier = Modifier.fillMaxWidth(),
                        status = item.interest.epStatus,
                        total = item.eps,
                        button = buildString {
                            if (firstUnCollectEp == null) append("Ep.已看完") else {
                                append("Ep.")
                                append(firstUnCollectEp.sortOrder.toTrimString())
                                append(doneActionText)
                            }
                        },
                        onClickIncrease = {
                            // 进度加一
                            if (firstUnCollectEp != null) {
                                onActionEvent(
                                    TrackingPageEvent.Action.OnUpdateEpisodeCollection(
                                        subject = item,
                                        episodes = listOf(firstUnCollectEp),
                                        type = CollectionEpisodeType.DONE
                                    )
                                )
                            }
                        }
                    )
                } else {
                    val nextEp = item.interest.epStatus + 1
                    val nextVol = item.interest.volStatus + 1

                    SubjectTrackingBar(
                        modifier = Modifier.fillMaxWidth(),
                        status = item.interest.epStatus,
                        total = item.eps,
                        button = buildString {
                            when {
                                item.eps == 0 -> append("Chap.$nextEp$doneActionText")
                                nextEp >= item.eps -> append("Chap.已完成")
                                else -> append("Chap.$nextEp$doneActionText")
                            }
                        },

                        onInputChangeConfirm = {
                            onActionEvent(
                                TrackingPageEvent.Action.OnUpdateSubjectCollection(
                                    subject = item,
                                    update = CollectionSubjectUpdate(epStatus = it)
                                )
                            )
                        },
                        onClickIncrease = {
                            onActionEvent(
                                TrackingPageEvent.Action.OnUpdateSubjectCollection(
                                    subject = item,
                                    update = CollectionSubjectUpdate(epStatus = nextEp)
                                )
                            )
                        }
                    )
                    SubjectTrackingBar(
                        modifier = Modifier.fillMaxWidth(),
                        status = item.interest.volStatus,
                        total = item.volumes,
                        button = buildString {
                            when {
                                item.volumes == 0 -> append("Vol.$nextVol$doneActionText")
                                nextVol >= item.volumes -> append("Vol.已看完")
                                else -> append("Vol.$nextVol$doneActionText")
                            }
                        },
                        onInputChangeConfirm = {
                            onActionEvent(
                                TrackingPageEvent.Action.OnUpdateSubjectCollection(
                                    subject = item,
                                    update = CollectionSubjectUpdate(volStatus = it)
                                )
                            )
                        },
                        onClickIncrease = {
                            onActionEvent(
                                TrackingPageEvent.Action.OnUpdateSubjectCollection(
                                    subject = item,
                                    update = CollectionSubjectUpdate(volStatus = nextVol)
                                )
                            )
                        }
                    )
                }
            }
        },
        supportingContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .offset(x = (-4).dp),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                TextButton(
                    modifier = Modifier.resetSize(),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    onClick = {}
                ) {
                    Text(text = "参与讨论")
                }
                TextButton(
                    modifier = Modifier.resetSize(),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    onClick = {},
                ) {
                    Text(text = "观吐槽")
                }
                TextButton(
                    modifier = Modifier.resetSize(),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    onClick = {}
                ) {
                    Text(text = "写长评")
                }
            }
        }
    )
}


@Composable
@Preview
private fun PreviewTrackingPageScreenContent() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        TrackingPageScreenContent(
            pagingItems = PreviewComposeSubjectLazyItems.collectAsLazyPagingItems(),
            state = TrackingPageState(
                showEp = true
            ),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}