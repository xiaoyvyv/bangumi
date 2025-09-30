package com.xiaoyv.bangumi.features.groups.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupMemberRole
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [GroupsDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class GroupsDetailState(
    val group: ComposeGroup = ComposeGroup.Empty,
    val tabs: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val memberFilters: SerializeList<ComposeTextTab<GroupMemberRole>> = persistentListOf(),
)
