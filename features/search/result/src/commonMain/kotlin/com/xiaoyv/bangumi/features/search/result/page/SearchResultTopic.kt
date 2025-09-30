package com.xiaoyv.bangumi.features.search.result.page

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_match
import com.xiaoyv.bangumi.core_resource.resources.global_show_summary
import com.xiaoyv.bangumi.core_resource.resources.global_sort
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState
import com.xiaoyv.bangumi.features.topic.page.TopicPageRoute
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.searchMatchTabs
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.searchSummaryTabs
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.searchTopicSort
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultTopic(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    val param = state.topicParam

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_sort),
                current = param.search.order,
                options = searchTopicSort,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchTopicParam(
                            body = param.search.copy(order = it.type),
                        )
                    )
                }
            )

            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_match),
                current = param.search.exact,
                options = searchMatchTabs,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchTopicParam(
                            body = param.search.copy(exact = it.type),
                        )
                    )
                }
            )

            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_show_summary),
                current = param.ui.showSummary,
                options = searchSummaryTabs,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateLayout(
                            ui = param.ui.copy(showSummary = it.type),
                        )
                    )
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            TopicPageRoute(
                param = state.topicParam,
                onNavScreen = {
                    onUiEvent(SearchResultEvent.UI.OnNavScreen(it))
                }
            )
        }
    }
}