package com.xiaoyv.common.helper

import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.Dispatchers

/**
 * Class: [NotifyHelper]
 *
 * @author why
 * @since 12/16/23
 */
object NotifyHelper {

    /**
     * 刷新 Notify
     */
    suspend fun refreshNotify() {
        runCatching {
            require(UserHelper.isLogin) { "未登录，不刷新通知心跳" }
            val notify = BgmApiManager.bgmWebApi.notify(System.currentTimeMillis())
            currentApplication.globalNotify.postValue(requireNotNull(notify.count) { "未登录" })
            debugLog { "定时任务：执行结果 -> $notify" }
        }.onFailure {
            debugLog { "定时任务：失败 -> ${it.message}" }
            it.printStackTrace()
        }
    }

    /**
     * 刷新通知已读
     */
    fun markAllRead() {
        launchProcess(Dispatchers.IO) {
            require(UserHelper.isLogin) { "未登录，不刷新通知" }
            currentApplication.globalNotify.sendValue(0)
            BgmApiManager.bgmWebApi.markNotifyRead(formHash = UserHelper.formHash)
            refreshNotify()
        }
    }
}