package com.xiaoyv.bangumi.features.subject.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun koinSubjectPageViewModel(param: ListSubjectParam): SubjectPageViewModel {
    return koinViewModel<SubjectPageViewModel>(key = param.uniqueKey) {
        parametersOf(param)
    }
}

/**
 * [SubjectPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SubjectPageViewModel(
    savedStateHandle: SavedStateHandle,
    private val subjectRepository: SubjectRepository,
    private val param: ListSubjectParam,
) : BaseViewModel<SubjectPageState, SubjectPageSideEffect, SubjectPageEvent.Action>(savedStateHandle) {

    private val subjectPager = subjectRepository.fetchSubjectPager(param)

    internal val subjects = subjectPager.flow
        .cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = SubjectPageState(
        param = param
    )

    override fun onEvent(event: SubjectPageEvent.Action) {

    }

}