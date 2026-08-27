package com.xiaoyv.bangumi.features.index.detail.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseMviViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinIndexDetailPageViewModel(param: ListIndexRelatedParam): IndexDetailPageViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

class IndexDetailPageViewModel(
    param: ListIndexRelatedParam,
    indexRepository: IndexRepository
) : BaseMviViewModel<IndexDetailPageState, Any, Any>() {
    private val indexRelatedPager = indexRepository.fetchIndexRelatePager(param)

    val indexRelated = indexRelatedPager.flow.cachedIn(viewModelScope)

    override fun createInitialState(): IndexDetailPageState {
        return IndexDetailPageState()
    }

    override fun onEvent(event: Any) {

    }
}