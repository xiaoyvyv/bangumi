package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.ui.component.button.collectionButtonColors
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyGridState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor

private const val CONTENT_TYPE_SUBJECT_SECTION = "CONTENT_TYPE_SUBJECT_SECTION"

@Composable
fun UserMainScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(50.dp),
        state = rememberCacheWindowLazyGridState(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
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
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun UserMainScreenSection(
    item: ComposeSection<ComposeSubject>,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    if (item.isHeader) {
        SectionTitle(
            modifier = Modifier
                .ignoreLazyGridContentPadding(12.dp)
                .padding(horizontal = LayoutPadding, vertical = LayoutPadding),
            text = item.header.title,
            subtitle = item.header.subtitle,
            action = item.header.more,
            onClick = {}
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
            Box {
                StateImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onUiEvent(UserEvent.UI.OnNavScreen(Screen.SubjectDetail(item.item.id))) },
                    model = item.item.images.displayMediumImage,
                    shape = MaterialTheme.shapes.small,
                )
                val colors = collectionButtonColors(item.item.interest.type)
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(colors.containerColor, MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    text = buildAnnotatedString {
                        append(CollectionType.string(item.item.type, item.item.interest.type))
                        append(" ")
                        if (item.item.interest.rate > 0) {
                            withSpanStyle(color = StarColor, fontWeight = FontWeight.SemiBold) {
                                append(item.item.interest.rate.toString())
                                append("分")
                            }
                        }
                    },
                    fontSize = 8.sp,
                    lineHeight = 8.sp,
                    color = colors.contentColor
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(Int.MAX_VALUE, spacing = MarqueeSpacing(4.dp)),
                text = item.item.displayName,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                maxLines = 1,
            )
        }
    }
}


