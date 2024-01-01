package com.xiaoyv.bangumi.ui.feature.notify

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.api.parser.impl.parserNotify
import com.xiaoyv.common.helper.NotifyHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [NotifyViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class NotifyViewModel : BaseListViewModel<NotifyEntity>() {

    override fun onViewCreated() {
        markAllRead()
    }

    override suspend fun onRequestListImpl(): List<NotifyEntity> {
        return BgmApiManager.bgmWebApi.queryNotifyAll().parserNotify()
    }

    private fun markAllRead() {
        NotifyHelper.markNotifyRead()
    }

    /**
     * 添加好友
     */
    fun addFriend(userId: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryUserInfo(userId)
                }
            }
        )
    }
}