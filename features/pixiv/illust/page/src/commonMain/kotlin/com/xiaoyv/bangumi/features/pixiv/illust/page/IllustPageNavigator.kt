package com.xiaoyv.bangumi.features.pixiv.illust.page

import com.xiaoyv.bangumi.features.pixiv.illust.page.business.IllustPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pixivIllustPageModule = module {
    viewModel { (param: ListIllustParam) ->
        IllustPageViewModel(pixivRepository = get(), param = param)
    }
}
