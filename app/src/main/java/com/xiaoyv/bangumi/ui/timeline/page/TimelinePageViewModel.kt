package com.xiaoyv.bangumi.ui.timeline.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.impl.parserTimelineForms
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
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
                val hasTargetUserId = userId.isNotBlank()
                if (timelineTab?.requireLogin == true) {
                    require(UserHelper.isLogin) { "你还没有登录呢" }
                }

                onTimelineLiveData.value = withContext(Dispatchers.IO) {
                    if (hasTargetUserId) {
                        BgmApiManager.bgmWebApi.queryUserTimeline(
                            userId = userId,
                            type = timelineType,
                            ajax = 0
                        ).parserTimelineForms(userId)
                    } else {
                        // 好友的时间线
                        if (ConfigHelper.isShowUserTimeline) {
                            BgmApiManager.bgmWebApi.queryFriendTimeline(
                                type = timelineType,
                                ajax = 1
                            ).parserTimelineForms()
                        }
                        // 全部的时间线
                        else {
                            BgmApiManager.bgmJsonApi.queryWholeTimeline(
                                type = timelineType,
                                ajax = 1
                            ).parserTimelineForms()
                        }
                    }
                }
            }
        )
    }
}