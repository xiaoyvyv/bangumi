package com.xiaoyv.bangumi.features.subject.page

import com.xiaoyv.bangumi.features.subject.page.business.SubjectPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val subjectPageModule = module {
    viewModel { (param: ListSubjectParam) ->
        SubjectPageViewModel(subjectRepository = get(), personalStateStore = get(), param = param)
    }
}
