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
import com.xiaoyv.bangumi.core_resource.resources.global_layout
import com.xiaoyv.bangumi.core_resource.resources.global_nsfw
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState
import com.xiaoyv.bangumi.shared.core.types.LayoutType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.filterNsfw
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.layoutTabs
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultCharacter(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    val param = state.characterParam

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_layout),
                current = if (param.ui.gridLayout) LayoutType.GRID else LayoutType.LIST,
                options = layoutTabs,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateLayout(
                            ui = state.characterParam.ui.copy(
                                gridLayout = it.type == LayoutType.GRID
                            )
                        )
                    )
                }
            )
            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_nsfw),
                current = param.search.filter.nsfw,
                options = filterNsfw,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchMonoParam(
                            body = param.search.copy(filter = param.search.filter.copy(nsfw = it.type)),
                            type = MonoType.CHARACTER
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
            MonoPageRoute(
                param = state.characterParam,
                onNavScreen = {
                    onUiEvent(SearchResultEvent.UI.OnNavScreen(it))
                }
            )
        }
    }
}