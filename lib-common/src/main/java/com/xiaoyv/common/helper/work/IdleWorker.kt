package com.xiaoyv.common.helper.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xiaoyv.common.helper.NotifyHelper
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
        NotifyHelper.refreshNotify()
        delay(30000)
        return Result.success()
    }
}

