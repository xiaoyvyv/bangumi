@file:OptIn(ExperimentalFoundationApi::class)

package com.xiaoyv.bangumi.features.main.tab.home.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.calendar_today_title
import com.xiaoyv.bangumi.core_resource.resources.calendar_tomorrow_title
import com.xiaoyv.bangumi.core_resource.resources.global_rank
import com.xiaoyv.bangumi.core_resource.resources.subject_home_calendar
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.core.types.FeatureType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomepageCard
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutGridWidth
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeActions
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectCardItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val CONTENT_TYPE_BANNER = "CONTENT_TYPE_BANNER"
private const val CONTENT_TYPE_ACTION = "CONTENT_TYPE_ACTION"
private const val CONTENT_TYPE_CALENDAR = "CONTENT_TYPE_CALENDAR"
private const val CONTENT_TYPE_CALENDAR_ITEM = "CONTENT_TYPE_CALENDAR_ITEM"
private const val CONTENT_TYPE_OVERVIEW = "CONTENT_TYPE_OVERVIEW"
private const val CONTENT_TYPE_OVERVIEW_ITEM = "CONTENT_TYPE_OVERVIEW_ITEM"


@Composable
fun HomeMainScreen(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "home_main_list" },
        state = rememberCacheWindowLazyListState(),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        item(key = CONTENT_TYPE_BANNER, contentType = CONTENT_TYPE_BANNER) {
            HomeMainBanner(state, onUiEvent, onActionEvent)
        }
        item(key = CONTENT_TYPE_ACTION, contentType = CONTENT_TYPE_ACTION) {
            HomeMainAction(state, onUiEvent, onActionEvent)
        }
        item(key = CONTENT_TYPE_CALENDAR, contentType = CONTENT_TYPE_CALENDAR) {
            HomeMainCalendar(state, onUiEvent, onActionEvent)
        }

        items(
            items = state.sections,
            contentType = { CONTENT_TYPE_OVERVIEW },
            key = { it.type }
        ) {
            HomeMainOverview(state, it, onUiEvent, onActionEvent)
        }
    }
}

@Composable
fun HomeMainBanner(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(state.hotSubjects) { display ->
            InfoImage(
                modifier = Modifier.fillMaxHeight(),
                model = display.subject.images.displayMediumImage,
                contentScale = ContentScale.Crop,
                text = display.subject.displayName,
                textMaxLines = 2,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                shape = RectangleShape,
                onClick = {
                    onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(display.subject.id)))
                }
            )
        }
    }
}

@Composable
fun HomeMainAction(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    val space = if (isExtraSmallScreen) 16.dp else 24.dp
    val columns = if (isExtraSmallScreen) SimpleGridCells.Fixed(5) else SimpleGridCells.Adaptive(50.dp)
    val scope = rememberCoroutineScope()

    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LayoutPadding),
        columns = columns,
        horizontalArrangement = Arrangement.spacedBy(space),
        verticalArrangement = Arrangement.spacedBy(space),
    ) {
        mainHomeActions.forEach {
            val label = stringResource(it.label)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .semantics { contentDescription = label }
                        .clip(CircleShape)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable {
                            when (it.type) {
                                FeatureType.TYPE_DETECT_ANIME -> onUiEvent(
                                    HomeEvent.UI.OnNavScreen(Screen.DetectImage(type = DetectType.SOURCE))
                                )

                                FeatureType.TYPE_DETECT_CHARACTER -> onUiEvent(
                                    HomeEvent.UI.OnNavScreen(Screen.DetectImage(type = DetectType.CHARACTER))
                                )

                                FeatureType.TYPE_ALMANAC -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Almanac))
                                FeatureType.TYPE_PIXIV -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.PixivLogin))
                                FeatureType.TYPE_SUBJECT_BROWSER -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectBrowser()))
                                FeatureType.TYPE_TAG -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.TagDetail()))
                                FeatureType.TYPE_SCHEDULE -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Calendar(true)))
                                FeatureType.TYPE_RANK -> scope.launch {
                                    onUiEvent(
                                        HomeEvent.UI.OnNavScreen(
                                            Screen.SubjectBrowser(
                                                body = SubjectBrowserBody(
                                                    sort = SubjectSortBrowserType.RANK,
                                                    subjectType = SubjectType.ANIME,
                                                    hideSortFilter = true
                                                ),
                                                title = getString(Res.string.global_rank),
                                            )
                                        )
                                    )
                                }

                                FeatureType.TYPE_TRACKING -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Tracking))
                                FeatureType.TYPE_NEWEST -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Newest))
                                FeatureType.TYPE_DOLLARS -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Dollars))
                                FeatureType.TYPE_TIMELINE -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Timeline))
                                FeatureType.TYPE_RAKUEN -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Topic))
                                FeatureType.TYPE_MAGNET -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Garden()))
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(maxWidth / 2.25f),
                        painter = painterResource(it.icon),
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HomeMainCalendar(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(
            modifier = Modifier.padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
            text = stringResource(Res.string.calendar_today_title),
            onClick = {
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Calendar(true)))
            }
        )

        Text(
            modifier = Modifier.padding(horizontal = LayoutPadding),
            text = stringResource(
                Res.string.subject_home_calendar,
                state.todayCalendar.size,
                state.todayTotal
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "calendar_card_row_today" },
            contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            items(
                items = state.todayCalendar,
                key = { it.subject.id },
                contentType = { CONTENT_TYPE_CALENDAR_ITEM }
            ) {
                SubjectCardItem(
                    modifier = Modifier
                        .width(LayoutGridWidth)
                        .semantics { contentDescription = "calendar_card_item" },
                    display = remember(it.subject.id) { ComposeSubjectDisplay(it.subject) },
                    maxLine = 1,
                    onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) },
                )
            }
        }

        Spacer(modifier = Modifier.height(LayoutPaddingHalf))

        SectionTitle(
            modifier = Modifier.padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
            text = stringResource(Res.string.calendar_tomorrow_title),
            onClick = {
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Calendar(false)))
            }
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "calendar_card_row_tomorrow" },
            contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            items(
                state.tomorrowCalendar,
                key = { it.subject.id },
                contentType = { CONTENT_TYPE_CALENDAR_ITEM }
            ) {
                SubjectCardItem(
                    modifier = Modifier
                        .width(LayoutGridWidth)
                        .semantics { contentDescription = "calendar_card_item" },
                    display = remember(it.subject.id) { ComposeSubjectDisplay(it.subject) },
                    maxLine = 1,
                    onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) },
                )
            }
        }
    }
}

@Composable
fun HomeMainOverview(
    state: HomeState,
    entity: ComposeHomepageCard,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle(
            modifier = Modifier.padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
            text = entity.title,
            onClick = {
                // 跳转注目的条目流量
                onUiEvent(
                    HomeEvent.UI.OnNavScreen(
                        Screen.SubjectBrowser(
                            body = SubjectBrowserBody(
                                subjectType = entity.type,
                                sort = SubjectSortBrowserType.TRENDS,
                                hideDateFilter = true
                            )
                        )
                    )
                )
            }
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "overview_card_row" },
            contentPadding = PaddingValues(LayoutPadding, LayoutPaddingHalf),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            items(
                entity.subjects,
                key = { it.id },
                contentType = { CONTENT_TYPE_OVERVIEW_ITEM }
            ) {
                SubjectCardItem(
                    modifier = Modifier
                        .width(LayoutGridWidth)
                        .semantics { contentDescription = "overview_card_item" },
                    display = remember(it.id) { ComposeSubjectDisplay(it) },
                    maxLine = 1,
                    onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
                )
            }
        }
    }
}







