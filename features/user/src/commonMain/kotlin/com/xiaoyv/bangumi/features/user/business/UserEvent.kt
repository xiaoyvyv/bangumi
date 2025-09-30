package com.xiaoyv.bangumi.features.user.business

import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [UserEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class UserEvent {
    sealed class UI : UserEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : UserEvent() {
        data class OnRefresh(val loading: Boolean) : Action()

        data class OnChangeSubjectTypeFilter(@field:SubjectType val type: Int) : Action()
        data class OnChangeCollectionTypeFilter(@field:CollectionType val type: Int) : Action()
        data class OnChangeCollectionSortFilter(@field:CollectionWebSortType val type: String) : Action()
    }
}