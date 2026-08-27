package com.xiaoyv.bangumi.features.index.page

import com.xiaoyv.bangumi.features.index.page.dialog.IndexDialogViewModel
import com.xiaoyv.bangumi.features.index.page.page.IndexPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.bgm.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val indexPageModule = module {
    viewModel { (param: ListIndexParam) ->
        IndexPageViewModel(get(), param = param)
    }
    viewModel { (target: IndexTarget) ->
        IndexDialogViewModel(indexRepository = get(), userManager = get(), target = target)
    }
}
