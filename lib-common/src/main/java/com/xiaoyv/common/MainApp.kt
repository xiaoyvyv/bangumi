package com.xiaoyv.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.H5PreLoadHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.work.IdleWorker
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.widget.emoji.UiFaceManager
import java.util.concurrent.TimeUnit

lateinit var currentApplication: MainApp

/**
 * Class: [MainApp]
 *
 * @author why
 * @since 11/24/23
 */
class MainApp : Application() {
    /**
     * 春菜全局说话
     */
    val globalRobotSpeech = MutableLiveData<String>()

    /**
     * 全局通知
     */
    val globalNotify = MutableLiveData<Int>()

    override fun onCreate() {
        super.onCreate()

        // 跟随壁纸主题
        if (ConfigHelper.isDynamicTheme) {
            DynamicColors.applyToActivitiesIfAvailable(this)
        }
        // 固定主题色
        else {
            DynamicColors.applyToActivitiesIfAvailable(
                this, DynamicColorsOptions.Builder()
                    .setContentBasedSource(getColor(CommonColor.seed))
                    .build()
            )
        }

        currentApplication = this
        BluePrint.init(this, false)
        H5PreLoadHelper.preloadWebView(this)
        UserHelper.initLoad()
        UiFaceManager.manager.initEmojiMap()
        startIdleWork()

        LogUtils.getConfig().isLogSwitch = AppUtils.isAppDebug()
    }

    private fun startIdleWork() {
        val workName = IdleWorker::class.simpleName.orEmpty()
        val workRequest = OneTimeWorkRequestBuilder<IdleWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS)
            .build()

        // 将 WorkRequest 加入队列
        WorkManager
            .getInstance(this)
            .enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, workRequest)

        // 观察工作状态并重新安排工作
        WorkManager
            .getInstance(this)
            .getWorkInfoByIdLiveData(workRequest.id)
            .observe(ProcessLifecycleOwner.get()) { workInfo ->
                if (workInfo != null && (workInfo.state == WorkInfo.State.SUCCEEDED || workInfo.state == WorkInfo.State.FAILED)) {
                    startIdleWork()
                }
            }
    }
}