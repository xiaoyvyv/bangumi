package com.xiaoyv.bangumi.features.garden.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.data.model.request.SearchMagnetBody
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [GardenEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class GardenEvent {
    sealed class UI : GardenEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : GardenEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnTextChanged(val value: TextFieldValue) : Action()
        data class OnChangeParamBody(val param: SearchMagnetBody) : Action()
        data object OnSearch : Action()
    }
}