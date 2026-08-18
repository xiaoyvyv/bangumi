package com.xiaoyv.bangumi.features.timeline.page

import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val timelinePageModule = module {
    viewModel { (param: ListTimelineParam) ->
        TimelinePageViewModel(ugcRepository = get(), param = param)
    }
}
