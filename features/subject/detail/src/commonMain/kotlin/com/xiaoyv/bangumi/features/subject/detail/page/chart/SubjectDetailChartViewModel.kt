package com.xiaoyv.bangumi.features.subject.detail.page.chart

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.syntax.Syntax


@Composable
fun koinSubjectDetailChartViewModel(subjectId: Long): SubjectDetailChartViewModel {
    return koinViewModel(
        key = subjectId.toString(),
        parameters = { parametersOf(subjectId) }
    )
}

class SubjectDetailChartViewModel(
    private val subjectRepository: SubjectRepository,
    private val subjectId: Long,
) : BaseViewModel<SubjectDetailChartState, Any, Any>() {
    override fun initBaseState(): UiState<SubjectDetailChartState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = SubjectDetailChartState()

    override fun onEvent(event: Any) = Unit

    override suspend fun Syntax<UiState<SubjectDetailChartState>, UiSideEffect<Any>>.refreshSync() {
        subjectRepository.fetchSubjectStats(subjectId)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData(forceRefresh = true) { state.copy(stats = it) }
            }
    }
}