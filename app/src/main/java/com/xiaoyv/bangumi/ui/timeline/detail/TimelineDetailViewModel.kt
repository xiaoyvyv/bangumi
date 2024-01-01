package com.xiaoyv.bangumi.ui.timeline.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TimelineDetailEntity
import com.xiaoyv.common.api.parser.entity.TimelineReplyEntity
import com.xiaoyv.common.api.parser.impl.parserTimelineDetail
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TimelineDetailViewModel]
 *
 * @author why
 * @since 1/1/24
 */
class TimelineDetailViewModel : BaseListViewModel<TimelineReplyEntity>() {
    internal var timelineId: String = ""
    internal var onTimelineDetailLiveData = MutableLiveData<TimelineDetailEntity?>()

    override val emptyCheck: Boolean
        get() = false

    override suspend fun onRequestListImpl(): List<TimelineReplyEntity> {
        val data = BgmApiManager.bgmWebApi
            .queryTimelineReply(UserHelper.currentUser.id, timelineId)
            .parserTimelineDetail()

        onTimelineDetailLiveData.postValue(data)

        return data.replies
    }

    fun replyTimeline(content: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postTimelineReply(
                        tmlId = timelineId,
                        content = content,
                        formHash = UserHelper.formHash
                    )
                }

                // 刷新
                refresh()
            }
        )
    }
}