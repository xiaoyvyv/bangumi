package com.xiaoyv.bangumi.features.garden.business

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import com.xiaoyv.bangumi.shared.data.model.request.SearchMagnetBody
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

/**
 * [GardenViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GardenViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.Garden,
    private val mikanRepository: MikanRepository,
) : BaseViewModel<GardenState, GardenSideEffect, GardenEvent.Action>(savedStateHandle) {
    private val argsParam = SearchMagnetBody(keyword = args.query)

    private val queryParam = mutableStateFlowOf(argsParam)

    val magnet = queryParam
        .flatMapLatest { mikanRepository.fetchGardenResourcePager(param = it).flow }
        .cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = GardenState(
        query = args.query.asTextFieldValue(),
        param = argsParam
    )

    override fun onEvent(event: GardenEvent.Action) {
        when (event) {
            is GardenEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is GardenEvent.Action.OnChangeParamBody -> onChangeParamBody(event.param)
            is GardenEvent.Action.OnTextChanged -> onTextChanged(event.value)
            is GardenEvent.Action.OnSearch -> onSearch()
        }
    }

    private fun onTextChanged(value: TextFieldValue) = action {
        reduceContent { state.copy(query = value) }
    }

    private fun onChangeParamBody(param: SearchMagnetBody) = action {
        reduceContent { state.copy(param = param) }
        queryParam.update { param }
    }

    private fun onSearch() = action {
        reduceContent { state.copy(param = state.param.copy(keyword = state.query.text.trim())) }
        queryParam.update { stateRaw.param }
    }
}