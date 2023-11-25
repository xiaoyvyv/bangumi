package com.xiaoyv.bangumi.ui.timeline.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.api.BgmWebApi
import com.xiaoyv.common.api.parser.impl.TimeParser.parserTimelineForms
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TimelinePageViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class TimelinePageViewModel : BaseViewModel() {
    internal var timelineTab: TimelineTab? = null

    internal val userId: String? = null

    internal val timelineType: String
        get() = timelineTab?.timelineType ?: TimelineType.TYPE_ALL

    internal val onTimelineLiveData = MutableLiveData<List<TimelineEntity>?>()

    fun queryTimeline() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                onTimelineLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryTimeline(
                        path = BgmWebApi.timelineUrl(userId),
                        type = timelineType
                    ).parserTimelineForms()
                }
            }
        )
    }
}