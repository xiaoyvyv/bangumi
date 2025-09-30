package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.timeline.page.web.TimelineWebPageScreen
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.types.list.ListTimelineType
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineWebParam
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.timelineWebTabs


@Composable
fun UserTimelineScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = timelineWebTabs
    ) {
        TimelineWebPageScreen(
            param = remember(it) {
                ListTimelineParam(
                    type = ListTimelineType.BROWSER_BY_WEB,
                    browserWeb = ListTimelineWebParam(
                        target = TimelineTarget.USER,
                        type = timelineWebTabs[it].type,
                        username = state.username
                    )
                )
            },
            onNavScreen = { screen ->
                onUiEvent(UserEvent.UI.OnNavScreen(screen))
            }
        )
    }
}

