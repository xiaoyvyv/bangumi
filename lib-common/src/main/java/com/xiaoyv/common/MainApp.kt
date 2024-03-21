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
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.base.BaseConfig
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.H5PreLoadHelper
import com.xiaoyv.common.helper.MiKanHelper
import com.xiaoyv.common.helper.SegmentHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.theme.ThemeHelper
import com.xiaoyv.common.helper.work.IdleWorker
import com.xiaoyv.common.widget.emoji.UiFaceManager
import com.xiaoyv.widget.adapt.autoConvertDensity
import com.xiaoyv.widget.webview.UiWebView
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
     *
     * - first: 电波提醒
     * - second: 短信
     */
    val globalNotify = MutableLiveData<Pair<Int, Int>>()

    private val adaptScreen by lazy {
        ConfigHelper.isAdaptScreen
    }

    override fun onCreate() {
        super.onCreate()
        currentApplication = this

        // 主题初始化
        ThemeHelper.instance.initTheme(this)

        // Base init
        initBaseConfig()

        // 框架初始化
        BluePrint.init(this, adaptScreen)
        if (adaptScreen) {
            UiWebView.getResourcesProxy = {
                it.autoConvertDensity()
            }
        }

        // H5 预加载
        H5PreLoadHelper.preloadWebView(this)

        // 用户数据
        UserHelper.initLoad()

        // 表情
        UiFaceManager.manager.initEmojiMap()

        // 心跳刷新通知
        startIdleWork()

        // 分词
        SegmentHelper.preload()

        // MiKan ID映射
        MiKanHelper.preload()

        // Log
        LogUtils.getConfig().isLogSwitch = AppUtils.isAppDebug()
    }

    private fun initBaseConfig() {
        BaseConfig.config.globalConfig = object : BaseConfig.OnConfigActivity {

        }
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