package com.xiaoyv.bangumi.ui.feature.schedule.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.response.api.ApiCalendarEntity

/**
 * Class: [SchedulePageViewModel]
 *
 * @author why
 * @since 3/20/24
 */
class SchedulePageViewModel : BaseListViewModel<ApiCalendarEntity.MediaItem>() {
    var scheduleData: ApiCalendarEntity.CalendarItem? = null

    override suspend fun onRequestListImpl(): List<ApiCalendarEntity.MediaItem> {
        return scheduleData?.items.orEmpty()
            .onEach { it.cover = it.images?.large.orEmpty().optImageUrl() }
            .sortedBy { it.airWeekday }
    }
}