package com.xiaoyv.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.BluePrint


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

    override fun onCreate() {
        super.onCreate()
        currentApplication = this
        BluePrint.init(this, false)
    }
}