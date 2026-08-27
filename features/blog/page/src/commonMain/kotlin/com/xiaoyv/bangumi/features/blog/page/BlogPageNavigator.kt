package com.xiaoyv.bangumi.features.blog.page

import com.xiaoyv.bangumi.features.blog.page.business.BlogPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val blogPageModule = module {
    viewModel { (param: ListBlogParam) ->
        BlogPageViewModel(param = param, blogRepository = get())
    }
}
