package com.xiaoyv.common.helper.callback

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.ArraySet
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.ThemeUtils
import com.xiaoyv.common.R
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.theme.ThemeHelper
import com.xiaoyv.common.helper.theme.ThemeType
import java.lang.ref.WeakReference

/**
 * Class: [ThemeActivityLifecycleCallback]
 *
 * @author why
 * @since 12/26/23
 */
@SuppressLint("RestrictedApi")
open class ThemeActivityLifecycleCallback : ActivityLifecycleCallbacks {
    private val sActivityDelegates = ArraySet<WeakReference<Activity>>()
    private val sActivityDelegatesLock = Object()

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (ConfigHelper.isSmoothFont) {
            // ThemeUtils.applyThemeOverlay(activity, R.style.Theme_Bangumi_Font)
        }

        when (ConfigHelper.appTheme) {
            ThemeType.TYPE_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            ThemeType.TYPE_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            ThemeType.TYPE_SYSTEM -> {

            }

            ThemeType.TYPE_WALLPAPER -> {
                ThemeHelper.instance.applyToActivityIfAvailable(
                    activity,
                    DynamicColorsOptions.Builder().build()
                )
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addActive(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
    }

    private fun addActive(delegate: Activity) {
        synchronized(sActivityDelegatesLock) {
            removeDelegateFromActives(delegate)
            // 将新记录添加到集合
            sActivityDelegates.add(WeakReference(delegate))
        }
    }

    private fun removeActivity(delegate: Activity) {
        synchronized(sActivityDelegatesLock) {
            // 删除指向集中委托的任何 WeakRef 记录
            removeDelegateFromActives(delegate)
        }
    }

    private fun removeDelegateFromActives(toRemove: Activity) {
        synchronized(sActivityDelegatesLock) {
            val i = sActivityDelegates.iterator()
            while (i.hasNext()) {
                val delegate = i.next().get()
                if (delegate === toRemove || delegate == null) {
                    // 如果委托是要删除的委托，或者它是 null（因为 WeakRef），将其从集合中移除
                    i.remove()
                }
            }
        }
    }

    /**
     * 重建全部页面
     */
    fun recreateAll() {
        synchronized(sActivityDelegatesLock) {
            for (activeDelegate in sActivityDelegates) {
                activeDelegate.get()?.recreate()
            }
        }
    }
}