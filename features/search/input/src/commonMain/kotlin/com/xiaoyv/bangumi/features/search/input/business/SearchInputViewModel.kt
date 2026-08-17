package com.xiaoyv.bangumi.features.search.input.business

import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.System
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.limit
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * [SearchInputViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SearchInputViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.SearchInput,
    private val subjectRepository: SubjectRepository,
) : BaseViewModel<SearchInputState, SearchInputSideEffect, SearchInputEvent.Action>(savedStateHandle) {
    private val search = mutableStateFlowOf(args.query)
    private val searchHistory = System.database.appSearchHistoryQueries
    private var searchSubmitted = false

    init {
        search
            .debounce(100)
            .flatMapLatest { query -> subjectRepository.fetchSearchSuggestion(query) }
            .onEach {
                it.onSuccess { suggestion ->
                    intent {
                        reduceData { state.copy(suggestions = suggestion.words.orEmpty()) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState() =
        SearchInputState(query = args.query.asTextFieldValue())

    override suspend fun Syntax<UiState<SearchInputState>, UiSideEffect<SearchInputSideEffect>>.refreshSync() {
        refreshHistory()
    }

    private fun refreshHistory() = intent {
        val histories = searchHistory.queryAllHistory().executeAsList()
            .map { it.keyword }
            .filter { it.isNotBlank() }

        reduceData { state.copy(histories = histories) }
    }

    override fun onEvent(event: SearchInputEvent.Action) {
        when (event) {
            is SearchInputEvent.Action.OnRefresh -> refresh(false)
            is SearchInputEvent.Action.OnQueryChange -> onQueryChange(event.query)
            is SearchInputEvent.Action.OnSearch -> onSearch()
            is SearchInputEvent.Action.OnClearHistory -> onClearHistory()
            is SearchInputEvent.Action.OnDeleteHistory -> onDeleteHistory(event.keyword)
        }
    }

    private fun onClearHistory() = intent {
        searchHistory.clearHistory()
        refreshHistory()
    }

    private fun onDeleteHistory(keyword: String) = intent {
        searchHistory.deleteHistory(keyword)
        refreshHistory()
    }

    private fun onQueryChange(value: TextFieldValue) = intent {
        val fieldValue = value.limit(50)
        reduceData { state.copy(query = fieldValue) }

        search.update { fieldValue.text.trim() }

        // refresh history
        if (fieldValue.text.isBlank()) {
            refreshHistory()
        }
    }

    private fun onSearch() = intent {
        if (searchSubmitted) return@intent

        val text = state.data.query.text.trim()
        if (text.isNotBlank()) {
            searchSubmitted = true
            searchHistory.deleteHistory(text)
            searchHistory.saveHistory(
                keyword = text,
                timestamp = getTimeMillis()
            )

            postEffect { SearchInputSideEffect.OnSearchResult(text) }
        }
    }
}
