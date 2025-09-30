package com.xiaoyv.bangumi.features.tag.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinTagPageViewModel(param: ListTagParam): TagPageViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [TagPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TagPageViewModel(
    savedStateHandle: SavedStateHandle,
    private val param: ListTagParam,
    private val subjectRepository: SubjectRepository,
) : BaseViewModel<TagPageState, TagPageSideEffect, TagPageEvent.Action>(savedStateHandle) {
    private val tagPager = subjectRepository.fetchSubjectTagPager(param)

    val tags = tagPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = TagPageState(
        keyword = param.search.keyword
    )

    override fun onEvent(event: TagPageEvent.Action) {
        when (event) {
            is TagPageEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}