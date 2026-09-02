package com.xiaoyv.bangumi.shared

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Process
import kotlin.system.exitProcess

open class AppApplication : Application() {
    private val activityList: MutableList<Activity> = ArrayList()

    override fun onCreate() {
        super.onCreate()
        application = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })
    }

    fun exitApp(kill: Boolean = false) {
        for (activity in activityList) activity.finish()
        activityList.clear()
        if (kill) {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }
}