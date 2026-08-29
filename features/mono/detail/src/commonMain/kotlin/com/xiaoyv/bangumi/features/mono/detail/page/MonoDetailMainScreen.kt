package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_collabs
import com.xiaoyv.bangumi.core_resource.resources.global_detail
import com.xiaoyv.bangumi.core_resource.resources.global_no_summary
import com.xiaoyv.bangumi.core_resource.resources.global_related_index
import com.xiaoyv.bangumi.core_resource.resources.global_spit_out
import com.xiaoyv.bangumi.core_resource.resources.global_summary
import com.xiaoyv.bangumi.core_resource.resources.mono_recently_character
import com.xiaoyv.bangumi.core_resource.resources.mono_recently_work
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoDetailTab
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoCollab
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.layout.box.MaxHeightFadeBox
import com.xiaoyv.bangumi.shared.ui.component.layout.state.CommentNoDataTip
import com.xiaoyv.bangumi.shared.ui.component.layout.state.itemKey
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.DetailSectionTitle
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.theme.ContentCoverWidth
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentReplyItem
import com.xiaoyv.bangumi.shared.ui.view.index.IndexCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectWorkItem
import org.jetbrains.compose.resources.stringResource

private const val ItemSummary = "KeySummary"
private const val ItemInfo = "KeyInfo"
private const val ItemPersonCharacter = "KeyPersonCharacter"
private const val ItemCharacterSubject = "KeyCharacterSubject"
private const val ItemCollab = "KeyCollab"
private const val ItemTitleCollab = "KeyTitleCollab"
private const val ItemIndex = "ItemIndex"
private const val ItemTitleComment = "TitleComment"
private const val ItemTitleCharacterSubject = "KeyTitleCharacterSubject"
private const val ItemTitlePersonCharacter = "KeyTitlePersonCharacter"
private const val ItemNoMore = "KeyNoMore"

/**
 * [MonoDetailMainScreen]
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailMainScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemKey(ItemSummary) {
            MonoDetailSummary(state, onUiEvent, onActionEvent)
        }
        itemKey(ItemInfo) {
            MonoDetailInfo(state, onUiEvent)
        }

        // 人员（CV）的最近出演角色
        if (state.casts.isNotEmpty()) {
            itemKey(ItemTitlePersonCharacter) {
                DetailSectionTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ContentMargin, bottom = ContentMarginHalf),
                    title = stringResource(Res.string.mono_recently_character),
                    action = stringResource(Res.string.subject_action_more),
                    onActionClick = {
                        onUiEvent(MonoDetailEvent.UI.OnSelectedPageType(MonoDetailTab.CASTS))
                    }
                )
            }

            items(
                items = state.casts,
                key = { if (state.type == MonoType.CHARACTER) it.subject.id else it.mono.id },
                contentType = { ItemPersonCharacter }
            ) {
                MonoCastItem(it, state, onUiEvent)
            }
        }

        // 人物最近作品
        if (state.works.isNotEmpty()) {
            itemKey(ItemTitleCharacterSubject) {
                DetailSectionTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ContentMargin, bottom = ContentMarginHalf),
                    title = stringResource(Res.string.mono_recently_work),
                    action = stringResource(Res.string.subject_action_more),
                    onActionClick = {
                        onUiEvent(MonoDetailEvent.UI.OnSelectedPageType(MonoDetailTab.WORKS))
                    }
                )
            }
            items(
                items = state.works,
                key = { it.subject.id },
                contentType = { ItemCharacterSubject }
            ) {
                SubjectWorkItem(
                    modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                    display = it,
                    onClick = {
                        onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id)))
                    }
                )
            }
        }

        // 合作者
        if (state.mono.webInfo.collabs.isNotEmpty()) {
            itemKey(ItemTitleCollab) {
                DetailSectionTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ContentMargin, bottom = ContentMarginHalf),
                    title = stringResource(Res.string.global_collabs),
                    action = stringResource(Res.string.subject_action_more),
                    onActionClick = {
                        onUiEvent(MonoDetailEvent.UI.OnSelectedPageType(MonoDetailTab.COLLABS))
                    }
                )
            }
            itemKey(ItemCollab) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    contentPadding = PaddingValues(horizontal = ContentMargin),
                    horizontalArrangement = Arrangement.spacedBy(ContentMargin)
                ) {
                    items(
                        state.mono.webInfo.collabs,
                        key = { it.id },
                        contentType = { "Collab" }
                    ) { collab ->
                        MonoDetailCollabPreviewItem(
                            collab = collab,
                            onClick = {
                                onUiEvent(
                                    MonoDetailEvent.UI.OnNavScreen(
                                        Screen.MonoDetail(collab.id, MonoType.PERSON)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        // 目录
        itemKey(ItemIndex, visible = state.mono.webInfo.indexList.isNotEmpty()) {
            MonoDetailIndexList(state, onUiEvent)
        }

        // 评论
        if (state.comments.isNotEmpty()) {
            itemKey(ItemTitleComment) {
                DetailSectionTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ContentMargin, bottom = ContentMarginHalf),
                    title = stringResource(Res.string.global_spit_out),
                )
            }
            itemsIndexed(state.comments) { index, item ->
                CommentReplyItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    level = 0,
                    isLikeable = true,
                    onClickUser = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClickReport = {
                        onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.Report(ReportType.USER, item.user.id)))
                    },
                    onClickReaction = {
                        onActionEvent(MonoDetailEvent.Action.OnReactionClick(item, it))
                    }
                )
                if (index != state.comments.lastIndex) HorizontalDivider()
            }

            itemKey(ItemNoMore) {
                CommentNoDataTip(isEmpty = state.comments.isEmpty())
            }
        }
    }
}


@Composable
private fun MonoDetailSummary(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_summary),
        onActionClick = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.summary))) }
    ) {
        MaxHeightFadeBox(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.summary))) },
            maxHeight = 300.dp,
        ) {
            BgmLinkedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin),
                text = state.mono.summary.ifBlank { stringResource(Res.string.global_no_summary) },
            )
        }
    }
}

@Composable
private fun MonoDetailInfo(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_detail),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.webInfo.info))) }
    ) {
        MaxHeightFadeBox(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.webInfo.info))) },
            maxHeight = 300.dp,
        ) {
            BgmLinkedText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin),
                text = state.mono.webInfo.info.ifBlank { stringResource(Res.string.global_no_summary) },
            )
        }
    }
}


@Composable
private fun MonoDetailIndexList(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
) {
    DetailSectionTitle(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ContentMargin),
        title = stringResource(Res.string.global_related_index),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = {
            onUiEvent(MonoDetailEvent.UI.OnSelectedPageType(MonoDetailTab.INDEX))
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
                state.mono.webInfo.indexList,
                key = { it.id },
                contentType = { "Index" }
            ) {
                IndexCardItem(
                    modifier = Modifier
                        .width(180.dp)
                        .fillParentMaxHeight(),
                    item = it,
                    onClick = {
                        onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.IndexDetail(it.id)))
                    }
                )
            }
        }
    }
}

@Composable
private fun MonoDetailCollabPreviewItem(
    collab: ComposeMonoCollab,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickWithoutRipped(onClick = onClick)
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        InfoImage(
            modifier = Modifier.width(ContentCoverWidth),
            model = collab.images.displayMediumImage,
            onClick = onClick,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = collab.name,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
        )

        if (collab.count.isNotBlank()) {
            Text(
                text = collab.count,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


@Preview
@Composable
private fun PreviewMonoDetailMainScreen() {
    PreviewColumn {
        MonoDetailMainScreen(
            state = MonoDetailState(1),
            onUiEvent = {},
            onActionEvent = {},
        )
    }
}

