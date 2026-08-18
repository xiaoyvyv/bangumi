package com.xiaoyv.bangumi.features.groups.page

import com.xiaoyv.bangumi.features.groups.page.business.GroupsPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val groupsPageModule = module {
    viewModel { (param: ListGroupParam) ->
        GroupsPageViewModel(groupRepository = get(), param = param)
    }
}
