package com.xiaoyv.bangumi.features.tag.page

import com.xiaoyv.bangumi.features.tag.page.business.TagPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tagPageModule = module {
    viewModel { (param: ListTagParam) ->
        TagPageViewModel(subjectRepository = get(), param = param)
    }
}
