package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_kind
import com.xiaoyv.bangumi.core_resource.resources.global_staff
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource


/**
 * [MonoDetailWorksScreen]
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailWorksScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        var selectedSubjectType by rememberSaveable { mutableIntStateOf(SubjectType.UNKNOWN) }
        var selectedPosition by rememberSaveable { mutableLongStateOf(0) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            DropMenuChip(
                options = TabTokens.subjectTypeAllTabs,
                labelPrefix = stringResource(Res.string.global_kind),
                current = selectedSubjectType,
                trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                onOptionClick = {
                    selectedSubjectType = it.type
                }
            )

            DropMenuChip(
                options = remember(state.positions.size) {
                    state.positions
                        .map { ComposeTextTab(type = it.type.id, labelText = it.type.displayName) }
                        .toPersistentList()
                },
                labelPrefix = stringResource(Res.string.global_staff),
                current = selectedPosition,
                trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                onOptionClick = {
                    selectedPosition = it.type
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SubjectPageRoute(
                param = state.rememberPersonWorkParam(selectedSubjectType, selectedPosition),
                onNavScreen = { screen ->
                    onUiEvent(MonoDetailEvent.UI.OnNavScreen(screen))
                }
            )
        }
    }
}