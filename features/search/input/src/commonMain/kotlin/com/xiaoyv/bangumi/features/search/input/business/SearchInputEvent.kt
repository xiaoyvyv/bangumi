package com.xiaoyv.bangumi.features.search.input.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SearchInputEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SearchInputEvent {
    sealed class UI : SearchInputEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SearchInputEvent() {
        data object OnSearch : Action()
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnClearHistory : Action()
        data class OnQueryChange(val query: TextFieldValue) : Action()
    }
}