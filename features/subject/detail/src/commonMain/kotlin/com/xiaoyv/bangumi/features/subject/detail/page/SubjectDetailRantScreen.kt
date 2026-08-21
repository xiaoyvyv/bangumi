package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.type_interest_doing
import com.xiaoyv.bangumi.core_resource.resources.type_interest_done
import com.xiaoyv.bangumi.core_resource.resources.type_interest_drop
import com.xiaoyv.bangumi.core_resource.resources.type_interest_hold
import com.xiaoyv.bangumi.core_resource.resources.type_interest_wish
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.page.rant.SubjectDetailRantPageRoute
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectRantFilters
import com.xiaoyv.bangumi.shared.ui.view.comment.LocalCommentSubjectType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList


/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailRantScreen(
    subjectId: Long,
    @SubjectType subjectType: Int,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
) {
    CompositionLocalProvider(LocalCommentSubjectType provides subjectType) {
        val tabs = subjectRantFilters
            .map { it.copy(labelText = CollectionType.string(subjectType, it.type), label = null) }
            .toPersistentList()

        BgmChipHorizontalPager(
            modifier = Modifier.fillMaxSize(),
            tabs = tabs,
        ) {
            SubjectDetailRantPageRoute(
                subjectId = subjectId,
                type = tabs[it].type,
                onUiEvent = onUiEvent,
            )
        }
    }
}
