package com.xiaoyv.bangumi.features.mono.page

import com.xiaoyv.bangumi.features.mono.page.business.MonoPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val monoPageModule = module {
    viewModel { (param: ListMonoParam) ->
        MonoPageViewModel(monoRepository = get(), param = param)
    }
}
