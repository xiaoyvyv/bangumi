package com.xiaoyv.bangumi.features.main.tab.profile.business

import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [ProfileEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class ProfileEvent {
    sealed class UI : ProfileEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : ProfileEvent() {
        data class OnChangeSubjectTypeFilter(@field:SubjectType val type: Int) : Action()
        data class OnChangeCollectionTypeFilter(@field:CollectionType val type: Int) : Action()
        data class OnChangeCollectionSortFilter(@field:CollectionWebSortType val type: String) : Action()
    }
}