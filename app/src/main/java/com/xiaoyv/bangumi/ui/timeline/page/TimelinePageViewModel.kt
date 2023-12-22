package com.xiaoyv.bangumi.ui.timeline.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.impl.parserTimelineForms
import com.xiaoyv.common.config.annotation.TimelinePageType
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
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

                onTimelineLiveData.value = withContext(Dispatchers.IO) {
                    // 指定用户的时间线
                    if (hasTargetUserId) {
                        return@withContext BgmApiManager.bgmWebApi.queryUserTimeline(
                            userId = userId,
                            type = timelineType,
                            ajax = 0
                        ).parserTimelineForms(userId)
                    }

                    // 未指定ID的情况
                    when (ConfigHelper.timelinePageType) {
                        // 全部好友的时间线
                        TimelinePageType.TYPE_FRIEND -> {
                            require(UserHelper.isLogin) { "你还没有登录呢" }
                            BgmApiManager.bgmWebApi.queryFriendTimeline(timelineType)
                                .parserTimelineForms()
                        }
                        // 自己的时间线
                        TimelinePageType.TYPE_MINE -> {
                            require(UserHelper.isLogin) { "你还没有登录呢" }
                            BgmApiManager.bgmWebApi.queryUserTimeline(
                                userId = UserHelper.currentUser.id.orEmpty(),
                                type = timelineType,
                                ajax = 0
                            ).parserTimelineForms(UserHelper.currentUser.id.orEmpty())
                        }
                        // 全部时间线
                        else -> BgmApiManager.bgmJsonApi.queryWholeTimeline(timelineType)
                            .parserTimelineForms()
                    }
                }
            }
        )
    }

    /**
     * 删除时间线
     */
    fun deleteTimeline(timelineId: String) {
        launchUI(
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.deleteTimeline(timelineId, UserHelper.formHash)
                }
            }
        )
    }
}