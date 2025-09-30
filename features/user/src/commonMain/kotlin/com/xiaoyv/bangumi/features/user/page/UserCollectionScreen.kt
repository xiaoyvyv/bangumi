package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.collections.immutable.toPersistentList

@Composable
fun UserCollectionScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listParam = state.rememberListSubjectParam(state.username)

        SubjectPageRoute(
            param = listParam,
            onNavScreen = { onUiEvent(UserEvent.UI.OnNavScreen(it)) },
            headerSticky = true,
            header = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    DropMenuChip(
                        options = TabTokens.collectionSortFilters,
                        current = state.selectedCollectSort,
                        trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                        onOptionClick = {
                            onActionEvent(UserEvent.Action.OnChangeCollectionSortFilter(it.type))
                        }
                    )

                    DropMenuChip(
                        options = TabTokens.subjectTypeFilters,
                        current = state.selectedSubjectType,
                        trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                        onOptionClick = {
                            onActionEvent(UserEvent.Action.OnChangeSubjectTypeFilter(it.type))
                        }
                    )

                    DropMenuChip(
                        options = TabTokens.collectionTypeFilters
                            .map { it.copy(labelText = CollectionType.string(state.selectedSubjectType, it.type)) }
                            .toPersistentList(),
                        current = state.selectedCollectType,
                        trailingIcon = { Icon(BgmIcons.ArrowDropDown, contentDescription = null) },
                        onOptionClick = {
                            onActionEvent(UserEvent.Action.OnChangeCollectionTypeFilter(it.type))
                        }
                    )
                }
            }
        )
    }
}

