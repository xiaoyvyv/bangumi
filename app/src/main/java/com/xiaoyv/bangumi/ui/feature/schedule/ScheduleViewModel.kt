package com.xiaoyv.bangumi.ui.feature.schedule

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [ScheduleViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class ScheduleViewModel : BaseViewModel() {
    internal var isShowToday: Boolean = false

    internal val onScheduleLiveData = MutableLiveData<ApiCalendarEntity?>()

    override fun onViewCreated() {
        querySchedule()
    }

    private fun querySchedule() {
        launchUI(
            stateView = loadingViewState,
            error = {

            },
            block = {
                onScheduleLiveData.value = withContext(Dispatchers.IO) {
                    val calendar = BgmApiManager.bgmJsonApi.queryCalendar()
                    // 如果某天没有数据，填充一个空的，要保证周一到周日七条数据
                    for (i in 1..7) {
                        checkCalendar(calendar, i)
                    }
                    calendar
                }
            }
        )
    }

    /**
     * 如果某天没有数据，填充一个空的，要保证周一到周日七条数据
     */
    private fun checkCalendar(calendar: ApiCalendarEntity, weekDay: Int) {
        if (calendar.find { it.weekday?.id == weekDay } == null) {
            calendar.add(
                ApiCalendarEntity.CalendarItem(
                    weekday = ApiCalendarEntity.Weekday(id = weekDay),
                    items = emptyList()
                )
            )
        }
    }
}