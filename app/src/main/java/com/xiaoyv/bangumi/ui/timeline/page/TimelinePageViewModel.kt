package com.xiaoyv.bangumi.ui.timeline.page

import com.xiaoyv.bangumi.base.BaseListViewModel
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
class TimelinePageViewModel : BaseListViewModel<TimelineEntity>() {
    internal var timelineTab: TimelineTab? = null

    /**
     * 是否指定了用户 ID
     */
    private val userId: String
        get() = timelineTab?.userId.orEmpty()

    private val timelineType: String
        get() = timelineTab?.timelineType ?: TimelineType.TYPE_ALL

    /**
     * 是否有多页
     */
    internal val hasMultiPage: Boolean
        get() {
            if (userId.isNotBlank()) return true
            val pageType = ConfigHelper.timelinePageType
            return pageType == TimelinePageType.TYPE_FRIEND || pageType == TimelinePageType.TYPE_MINE
        }

    override suspend fun onRequestListImpl(): List<TimelineEntity> {
        val hasTargetUserId = userId.isNotBlank()

        // 指定用户的时间线
        if (hasTargetUserId) {
            return BgmApiManager.bgmWebApi.queryUserTimeline(
                userId = userId,
                type = timelineType,
                page = current,
                ajax = 0
            ).parserTimelineForms(userId)
        }

        // 未指定ID的情况
        return when (ConfigHelper.timelinePageType) {
            // 全部好友的时间线
            TimelinePageType.TYPE_FRIEND -> {
                require(UserHelper.isLogin) { "你还没有登录呢" }
                BgmApiManager.bgmWebApi.queryFriendTimeline(
                    type = timelineType,
                    page = current,
                    ajax = 0
                ).parserTimelineForms()
            }
            // 自己的时间线
            TimelinePageType.TYPE_MINE -> {
                require(UserHelper.isLogin) { "你还没有登录呢" }
                BgmApiManager.bgmWebApi.queryUserTimeline(
                    userId = UserHelper.currentUser.id.orEmpty(),
                    type = timelineType,
                    page = current,
                    ajax = 0
                ).parserTimelineForms(UserHelper.currentUser.id.orEmpty())
            }
            // 全部时间线
            else -> BgmApiManager.bgmJsonApi.queryWholeTimeline(
                type = timelineType,
                page = current
            ).parserTimelineForms(isTotalTimeline = true)
        }
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