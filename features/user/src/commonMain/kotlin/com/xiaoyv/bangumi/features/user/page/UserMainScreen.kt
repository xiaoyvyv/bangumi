package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.profile_rating_score
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyGridState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.user.UserCollectionHighlightCard
import org.jetbrains.compose.resources.stringResource

private const val CONTENT_TYPE_SUBJECT_SECTION = "CONTENT_TYPE_SUBJECT_SECTION"

/**
 * 用户主页时间光机页面，按条目类型分块展示最近收藏。
 */
@Composable
fun UserMainScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
    onOpenCollection: (Int) -> Unit = {},
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(132.dp),
        state = rememberCacheWindowLazyGridState(),
        contentPadding = PaddingValues(
            start = ContentMarginHalf,
            top = ContentMarginHalf,
            end = ContentMarginHalf,
            bottom = 40.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
    ) {
        items(
            items = state.timeMachine,
            key = { it.key },
            span = { if (it.isHeader) GridItemSpan(maxLineSpan) else GridItemSpan(1) },
            contentType = { CONTENT_TYPE_SUBJECT_SECTION }
        ) {
            UserMainScreenSection(
                item = it,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent,
                onOpenCollection = onOpenCollection
            )
        }
    }
}

/**
 * 根据分区类型渲染块头或条目卡片。
 */
@Composable
private fun UserMainScreenSection(
    item: ComposeSection<ComposeSubject>,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
    onOpenCollection: (Int) -> Unit,
) {
    if (item.isHeader) {
        SectionTitle(
            modifier = Modifier
                .ignoreLazyGridContentPadding(ContentMarginHalf)
                .padding(horizontal = ContentMarginHalf, vertical = ContentMarginHalf),
            text = item.header.title,
            subtitle = item.header.subtitle,
            action = item.header.more,
            onClick = {
                val subjectType = item.header.id.toIntOrNull() ?: SubjectType.ANIME
                onOpenCollection(subjectType)
            }
        )
    } else {
        UserCollectionHighlightCard(
            modifier = Modifier.aspectRatio(0.76f),
            title = item.item.displayName,
            imageUrl = item.item.images.displayMediumImage,
            badge = CollectionType.string(item.item.type, item.item.interest.type),
            badgeType = item.item.interest.type,
            score = if (item.item.interest.rate > 0) {
                stringResource(Res.string.profile_rating_score, item.item.interest.rate)
            } else {
                ""
            },
            onClick = {
                onUiEvent(UserEvent.UI.OnNavScreen(Screen.SubjectDetail(item.item.id)))
            }
        )
    }
}
