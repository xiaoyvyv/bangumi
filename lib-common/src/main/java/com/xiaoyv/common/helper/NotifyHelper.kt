package com.xiaoyv.common.helper

import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MessageCountEntity
import com.xiaoyv.common.api.parser.impl.parserCountInfo
import com.xiaoyv.common.api.response.NotifyEntity
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

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
            val (data1, data2) = coroutineScope {
                awaitAll(
                    async {
                        BgmApiManager.bgmWebApi.notify(System.currentTimeMillis())
                    },
                    async {
                        BgmApiManager.bgmWebApi.queryMessageList(MessageBoxType.TYPE_INBOX, 1)
                            .parserCountInfo()
                    }
                )
            }

            val notifyCount = requireNotNull((data1 as NotifyEntity).count) { "未登录" }
            val messageCount = (data2 as MessageCountEntity).unRead

            currentApplication.globalNotify.postValue(notifyCount to messageCount)
            debugLog { "定时任务：执行结果 -> notifyCount: $notifyCount, messageCount: $messageCount" }
        }.onFailure {
            debugLog { "定时任务：失败 -> ${it.message}" }
            it.printStackTrace()
        }
    }

    /**
     * 刷新通知已读
     */
    fun markNotifyRead() {
        launchProcess(Dispatchers.IO) {
            require(UserHelper.isLogin) { "未登录，不刷新通知" }
            BgmApiManager.bgmWebApi.markNotifyRead(formHash = UserHelper.formHash)
            refreshNotify()
        }
    }

    /**
     * 短信通知数目刷新直接重新拉取即可
     */
    fun markMessageRead() {
        launchProcess(Dispatchers.IO) {
            require(UserHelper.isLogin) { "未登录，不刷新通知" }
            refreshNotify()
        }
    }
}