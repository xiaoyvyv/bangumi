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
import com.xiaoyv.bangumi.core_resource.resources.global_kind
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState
import com.xiaoyv.bangumi.features.tag.page.TagPageRoute
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectTypeTabs
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultTag(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    val param = state.tagParam

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_kind),
                current = param.search.subjectType,
                options = subjectTypeTabs,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchTagParam(
                            body = param.search.copy(subjectType = it.type),
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
            TagPageRoute(
                param = state.tagParam,
                onNavScreen = {
                    onUiEvent(SearchResultEvent.UI.OnNavScreen(it))
                }
            )
        }
    }
}