package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
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
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.itemKey
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.DetailSectionTitle
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentItem
import com.xiaoyv.bangumi.shared.ui.view.index.IndexCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectWorkItem
import org.jetbrains.compose.resources.stringResource

private const val ItemSummary = "KeySummary"
private const val ItemInfo = "KeyInfo"
private const val ItemPersonCharacter = "KeyPersonCharacter"
private const val ItemCharacterSubject = "KeyCharacterSubject"
private const val ItemIndex = "ItemIndex"
private const val ItemTitleComment = "TitleComment"
private const val ItemTitleCharacterSubject = "KeyTitleCharacterSubject"
private const val ItemTitlePersonCharacter = "KeyTitlePersonCharacter"

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
                        .padding(top = LayoutPadding, bottom = LayoutPaddingHalf),
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
                        .padding(top = LayoutPadding, bottom = LayoutPaddingHalf),
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
                    modifier = Modifier.padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                    display = it,
                    onClick = {
                        onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id)))
                    }
                )
            }
        }

        // 目录
        itemKey(ItemIndex, visible = state.mono.webInfo.indexList.isNotEmpty()) {
            MonoDetailIndexList(state, onUiEvent)
        }

        // 评论
        if (state.mono.webInfo.comments.isNotEmpty()) {
            itemKey(ItemTitleComment) {
                DetailSectionTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LayoutPadding, bottom = LayoutPaddingHalf),
                    title = stringResource(Res.string.global_spit_out),
                )
            }
            itemsIndexed(state.mono.webInfo.comments) { index, item ->
                if (index > 0 && item.parent == null) BgmHorizontalDivider()

                CommentItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    onClickUser = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
                    onClick = {}
                )
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
            .padding(vertical = LayoutPadding),
        title = stringResource(Res.string.global_summary),
        onActionClick = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.summary))) }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.summary))) }
                .padding(horizontal = LayoutPadding),
            text = state.mono.summary.ifBlank { stringResource(Res.string.global_no_summary) },
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 10,
            minLines = 5,
            overflow = TextOverflow.Ellipsis
        )
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
            .padding(vertical = LayoutPadding),
        title = stringResource(Res.string.global_detail),
        action = stringResource(Res.string.subject_action_more),
        onActionClick = { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.webInfo.info))) }
    ) {
        BgmLinkedText(
            modifier = Modifier
                .fillMaxWidth()
                .clickWithoutRipped { onUiEvent(MonoDetailEvent.UI.OnNavScreen(Screen.PreviewText(state.mono.webInfo.info))) }
                .padding(horizontal = LayoutPadding),
            text = state.mono.webInfo.shortInfoHtml,
            maxLines = 10,
            minLines = 5,
        )
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
            .padding(vertical = LayoutPadding),
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
            contentPadding = PaddingValues(horizontal = LayoutPadding),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
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