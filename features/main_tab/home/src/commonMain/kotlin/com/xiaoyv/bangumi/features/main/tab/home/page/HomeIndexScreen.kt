package com.xiaoyv.bangumi.features.main.tab.home.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.index.page.page.IndexPageRoute
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeIndexFilters
import com.xiaoyv.bangumi.shared.ui.view.index.IndexFocusCard
import kotlinx.collections.immutable.toPersistentList

@Composable
fun HomeIndexScreen(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = mainHomeIndexFilters,
    ) {
        IndexPageRoute(
            param = state.rememberListIndexParam(mainHomeIndexFilters[it].type),
            onNavScreen = { screen -> onUiEvent(HomeEvent.UI.OnNavScreen(screen)) }
        )
    }
}

@Composable
private fun HomeIndexFocusHeader(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .aspectRatio(3f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val items = remember(state.indexFocus) { state.indexFocus.take(3) }

        items.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .border(4.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                IndexFocusCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, MaterialTheme.colorScheme.onSurface)
                        .shadow(1.dp),
                    images = it.images.take(13).toPersistentList()
                )

                Text(text = it.title)
            }
        }
    }
}
