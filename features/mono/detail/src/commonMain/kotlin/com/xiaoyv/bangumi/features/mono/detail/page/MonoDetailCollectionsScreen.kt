package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI

/**
 * [MonoDetailCollectionsScreen]
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailCollectionsScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    FriendRoute(
        param = remember {
            if (state.type == MonoType.CHARACTER) {
                ListUserParam(
                    type = ListUserType.CHARACTER_COLLECT,
                    characterID = state.id,
                    ui = PageUI(pageMode = true)
                )
            } else {
                ListUserParam(
                    type = ListUserType.PERSON_COLLECT,
                    personID = state.id,
                    ui = PageUI(pageMode = true)
                )
            }
        },
        onNavScreen = { screen -> onUiEvent(MonoDetailEvent.UI.OnNavScreen(screen)) }
    )
}