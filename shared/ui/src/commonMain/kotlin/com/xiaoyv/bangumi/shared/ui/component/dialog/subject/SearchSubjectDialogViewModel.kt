package com.xiaoyv.bangumi.shared.ui.component.dialog.subject

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job

/**
 * 条目搜索弹窗的状态管理。
 *
 * @param subjectRepository 条目数据仓库
 */
class SearchSubjectDialogViewModel(
    private val subjectRepository: SubjectRepository,
) : BaseViewModel<SearchSubjectDialogState, SearchSubjectDialogSideEffect, SearchSubjectDialogEvent>() {
    private var searchJob: Job? = null
    override fun createInitialState() = SearchSubjectDialogState()

    override fun onEvent(event: SearchSubjectDialogEvent) {
        when (event) {
            is SearchSubjectDialogEvent.OnQueryChange -> onSearchSubject(event.query)
        }
    }

    private fun onSearchSubject(query: TextFieldValue) {
        searchJob?.cancel()
        searchJob = intent {
            reduceData { state.copy(query = query) }

            if (query.text.isBlank()) {
                reduceData { state.copy(subjects = persistentListOf()) }
                return@intent
            }

            subjectRepository.fetchSubjectList(
                param = ListSubjectParam(
                    type = ListSubjectType.SEARCH,
                    search = SubjectSearchBody(keyword = query.text),
                ),
                offset = 0,
                pageSize = 20,
            ).onFailure {
                postToast { it.errMsg }
            }.onSuccess { result ->
                reduceData { state.copy(subjects = result.map { it.subject }.toPersistentList()) }
            }
        }
    }
}
