package com.xiaoyv.common.helper.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.delay

/**
 * Class: [IdleWorker]
 *
 */
class IdleWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    /**
     * 定时任务
     */
    override suspend fun doWork(): Result {
        runCatching {
            val notify = BgmApiManager.bgmWebApi.notify(System.currentTimeMillis())
            currentApplication.globalNotify.postValue(requireNotNull(notify.count) { "未登录" })
            debugLog { "定时任务：执行结果 -> $notify" }
        }.onFailure {
            debugLog { "定时任务：失败 -> ${it.message}" }
            it.printStackTrace()
        }
        delay(30000)
        return Result.success()
    }
}

