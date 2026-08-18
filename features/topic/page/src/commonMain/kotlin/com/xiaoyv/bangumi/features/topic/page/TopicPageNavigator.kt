package com.xiaoyv.bangumi.features.topic.page

import com.xiaoyv.bangumi.features.topic.page.business.TopicPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val topicPageModule = module {
    viewModel { (param: ListTopicParam) ->
        TopicPageViewModel(param = param, topicRepository = get())
    }
}
