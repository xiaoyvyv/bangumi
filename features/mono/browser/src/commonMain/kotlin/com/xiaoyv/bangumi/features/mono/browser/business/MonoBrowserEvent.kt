package com.xiaoyv.bangumi.features.mono.browser.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MonoBrowserEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MonoBrowserEvent {
    sealed class UI : MonoBrowserEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MonoBrowserEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnChangeFilterOrderBy(val orderBy: String) : Action()
        data class OnChangeFilterType(val type: String) : Action()
        data class OnChangeFilterGender(val gender: String) : Action()
        data class OnChangeFilterBloodType(val bloodType: String) : Action()
        data class OnChangeFilterMonth(val month: String) : Action()
    }
}