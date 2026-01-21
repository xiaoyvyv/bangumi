package com.xiaoyv.bangumi.features.search.input.business

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
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

    init {
        search
            .debounce(100)
            .flatMapLatest { query -> subjectRepository.fetchSearchSuggestion(query) }
            .onEach {
                it.onSuccess { suggestion ->
                    action {
                        reduceContent { state.copy(suggestions = suggestion.words.orEmpty()) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun initSate(onCreate: Boolean) =
        SearchInputState(query = args.query.asTextFieldValue())

    override suspend fun BaseSyntax<SearchInputState, SearchInputSideEffect>.refreshSync() {
        refreshHistory()
    }

    private fun refreshHistory() = action {
        val histories = searchHistory.queryAllHistory(limit = 10).executeAsList()
            .map { it.keyword }
            .filter { it.isNotBlank() }

        reduceContent { state.copy(histories = histories) }
    }

    override fun onEvent(event: SearchInputEvent.Action) {
        when (event) {
            is SearchInputEvent.Action.OnRefresh -> refresh(false)
            is SearchInputEvent.Action.OnQueryChange -> onQueryChange(event.query)
            is SearchInputEvent.Action.OnSearch -> onSearch()
            is SearchInputEvent.Action.OnClearHistory -> onClearHistory()
        }
    }

    private fun onClearHistory() = action {
        searchHistory.clearHistory()
        refreshHistory()
    }

    private fun onQueryChange(value: TextFieldValue) = action {
        val fieldValue = value.limit(50)
        reduceContent { state.copy(query = fieldValue) }

        search.update { fieldValue.text.trim() }

        // refresh history
        if (fieldValue.text.isBlank()) {
            refreshHistory()
        }
    }

    private fun onSearch() = action {
        val text = state.content.query.text.trim()
        if (text.isNotBlank()) {
            searchHistory.deleteHistory(text)
            searchHistory.saveHistory(
                keyword = text,
                timestamp = getTimeMillis()
            )

            postEffect { SearchInputSideEffect.OnSearchResult(text) }
        }
    }
}