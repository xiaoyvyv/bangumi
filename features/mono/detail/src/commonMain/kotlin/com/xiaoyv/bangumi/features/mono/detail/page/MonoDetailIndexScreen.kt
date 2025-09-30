package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.features.index.page.page.IndexPageRoute
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.data.model.request.list.index.IndexRelatedBody
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam

@Composable
fun MonoDetailIndexScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    IndexPageRoute(
        param = remember {
            ListIndexParam(
                type = if (state.type == MonoType.CHARACTER) ListIndexType.CHARACTER_RELATED else ListIndexType.PERSON_RELATED,
                related = IndexRelatedBody(monoId = state.id)
            )
        },
        onNavScreen = { screen -> onUiEvent(MonoDetailEvent.UI.OnNavScreen(screen)) },
    )
}
