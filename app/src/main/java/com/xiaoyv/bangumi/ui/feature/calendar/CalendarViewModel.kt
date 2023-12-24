package com.xiaoyv.bangumi.ui.feature.calendar

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [CalendarViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class CalendarViewModel : BaseViewModel() {

    internal val onCalendarLiveData = MutableLiveData<ApiCalendarEntity?>()
    internal var isShowToday: Boolean = false

    override fun onViewCreated() {

        queryCalendar()
    }

    fun queryCalendar() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onCalendarLiveData.value = null
            },
            block = {
                onCalendarLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryCalendar()
                }
            }
        )
    }
}