@file:OptIn(ExperimentalFoundationApi::class)

package com.xiaoyv.bangumi.features.main.tab.home.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.calendar_today_title
import com.xiaoyv.bangumi.core_resource.resources.calendar_tomorrow_title
import com.xiaoyv.bangumi.core_resource.resources.global_rank
import com.xiaoyv.bangumi.core_resource.resources.subject_home_calendar
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.FeatureType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomepageCard
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.layout.adaptive.AdaptiveGrid
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutGridWidth
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeActions
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
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
    uiState: UiState<HomeState>,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        enablePullRefresh = true,
        onRefresh = { onActionEvent(HomeEvent.Action.OnRefresh(it)) },
    ) { state ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "home_main_list" },
            state = rememberCacheWindowLazyListState(),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
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
    val scope = rememberCoroutineScope()

    AdaptiveGrid(
        minColumnWidth = 50.dp,
        horizontalSpacing = space,
        fixedColumnCount = if (isExtraSmallScreen) 5 else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(ContentMargin),
    ) {
        mainHomeActions.forEach {
            val label = stringResource(it.label)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .semantics { contentDescription = label }
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = MaterialTheme.shapes.large,
                    onClick = {
                        when (it.type) {
                            FeatureType.TYPE_DETECT_ANIME -> onUiEvent(
                                HomeEvent.UI.OnNavScreen(Screen.DetectImage(type = DetectType.SOURCE))
                            )

                            FeatureType.TYPE_DETECT_CHARACTER -> onUiEvent(
                                HomeEvent.UI.OnNavScreen(Screen.DetectImage(type = DetectType.CHARACTER))
                            )

                            FeatureType.TYPE_ALMANAC -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Almanac))
                            FeatureType.TYPE_PIXIV -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.PixivMain))
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
                            FeatureType.TYPE_RAKUEN -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.RaKuen))
                            FeatureType.TYPE_MAGNET -> onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Garden()))
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize(1f / 1.5f)
                                .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.large)
                                .padding(8.dp),
                            painter = painterResource(it.icon),
                            contentDescription = label,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
            modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
            text = stringResource(Res.string.calendar_today_title),
            onClick = {
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Calendar(true)))
            }
        )

        Text(
            modifier = Modifier.padding(horizontal = ContentMargin),
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
            contentPadding = PaddingValues(ContentMargin, ContentMarginHalf),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
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
                    display = remember(it.subject.id) { ComposeSubjectRelation(it.subject) },
                    maxLine = 1,
                    onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.subject.id))) },
                )
            }
        }

        Spacer(modifier = Modifier.height(ContentMarginHalf))

        SectionTitle(
            modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
            text = stringResource(Res.string.calendar_tomorrow_title),
            onClick = {
                onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Calendar(false)))
            }
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "calendar_card_row_tomorrow" },
            contentPadding = PaddingValues(ContentMargin, ContentMarginHalf),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
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
                    display = remember(it.subject.id) { ComposeSubjectRelation(it.subject) },
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
            modifier = Modifier.padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
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
            contentPadding = PaddingValues(ContentMargin, ContentMarginHalf),
            horizontalArrangement = Arrangement.spacedBy(ContentMargin)
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
                    display = remember(it.id) { ComposeSubjectRelation(it) },
                    maxLine = 1,
                    onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
                )
            }
        }
    }
}
