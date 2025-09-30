package com.xiaoyv.bangumi.features.blog.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinBlogPageViewModel(param: ListBlogParam): BlogPageViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [BlogPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class BlogPageViewModel(
    savedStateHandle: SavedStateHandle,
    param: ListBlogParam,
    ugcRepository: UgcRepository,
) : BaseViewModel<BlogPageState, BlogPageSideEffect, BlogPageEvent.Action>(savedStateHandle) {

    private val blogPager = ugcRepository.fetchBlogPager(param)

    val blog = blogPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = BlogPageState()

    override fun onEvent(event: BlogPageEvent.Action) {

    }
}