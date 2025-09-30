package com.xiaoyv.bangumi.features.search.result.page

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.features.mono.page.MonoPageRoute
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState

@Composable
fun SearchResultPerson(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    MonoPageRoute(
        param = state.personParam,
        onNavScreen = {
            onUiEvent(SearchResultEvent.UI.OnNavScreen(it))
        }
    )
}
