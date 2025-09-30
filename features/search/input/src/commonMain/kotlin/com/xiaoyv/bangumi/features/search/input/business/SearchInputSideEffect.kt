package com.xiaoyv.bangumi.features.search.input.business

/**
 * [SearchInputSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SearchInputSideEffect {
    data class OnSearchResult(val value: String) : SearchInputSideEffect()
}