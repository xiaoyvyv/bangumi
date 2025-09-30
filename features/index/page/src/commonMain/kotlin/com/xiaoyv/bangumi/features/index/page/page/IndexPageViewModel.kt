package com.xiaoyv.bangumi.features.index.page.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinIndexPageViewModel(param: ListIndexParam): IndexPageViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

class IndexPageViewModel(ugcRepository: UgcRepository, param: ListIndexParam) : ViewModel() {
    private val indexPager = ugcRepository.fetchIndexPager(param)
    val index = indexPager.flow.cachedIn(viewModelScope)
}

