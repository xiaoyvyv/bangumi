package com.xiaoyv.bangumi.ui.timeline.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.impl.parserTimelineForms
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

    /**
     * 是否指定了用户 ID
     */
    private val userId: String
        get() = timelineTab?.userId.orEmpty()

    private val timelineType: String
        get() = timelineTab?.timelineType ?: TimelineType.TYPE_ALL

    internal val onTimelineLiveData = MutableLiveData<List<TimelineEntity>?>()

    fun queryTimeline() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                val forUser = userId.isNotBlank()

                onTimelineLiveData.value = withContext(Dispatchers.IO) {
                    if (forUser) {
                        BgmApiManager.bgmWebApi.queryTimeline(
                            userId = userId,
                            type = timelineType,
                            ajax = 0
                        ).parserTimelineForms(userId)
                    } else {
                        BgmApiManager.bgmJsonApi.queryWholeTimeline(
                            type = timelineType,
                            ajax = 1
                        ).parserTimelineForms()
                    }
                }
            }
        )
    }
}