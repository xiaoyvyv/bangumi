package com.xiaoyv.bangumi.ui.timeline

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TimelineViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class TimelineViewModel : BaseViewModel() {

    /**
     * 吐个槽
     */
    fun addTimelineComment(timelineComment: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postTimelineSay(
                        sayInput = timelineComment,
                        gh = UserHelper.formHash
                    )
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_TIMELINE)
            }
        )
    }
}