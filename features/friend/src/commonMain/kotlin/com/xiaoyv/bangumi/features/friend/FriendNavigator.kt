package com.xiaoyv.bangumi.features.friend

import com.xiaoyv.bangumi.features.friend.business.FriendViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val friendModule = module {
    viewModel { (param: ListUserParam) ->
        FriendViewModel(userRepository = get(), param = param)
    }
}
