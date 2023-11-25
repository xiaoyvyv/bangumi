package com.xiaoyv.bangumi.ui.feature.floater

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.bangumi.MainActivity
import com.xiaoyv.bangumi.ui.feature.musmme.MusumeFloater
import com.xiaoyv.floater.FloatyService
import com.xiaoyv.floater.FloatyWindow
import java.lang.ref.WeakReference

/**
 * Created by Stardust on 2017/9/30.
 */
object FloatingWindowManger {
    private var screenColorPickerThumb: WeakReference<MusumeFloater>? = null

    fun init(context: Context) {
        FloatyService.start(
            context,
            foregroundNotificationClickClass = MainActivity::class.java.name,
            foregroundNotificationChannelName = context.getString(com.xiaoyv.common.R.string.foreground_notification_channel_name),
            foregroundNotificationTitle = context.getString(
                com.xiaoyv.common.R.string.foreground_notification_title,
                context.getString(com.xiaoyv.common.R.string.app_name)
            ),
            foregroundNotificationText = context.getString(com.xiaoyv.common.R.string.foreground_notification_text),
            foregroundNotificationIcon = com.xiaoyv.common.R.drawable.ic_icon,
        )
    }

    fun addWindow(context: Context, window: FloatyWindow): Boolean {
        val hasPermission = FloatingPermission.ensurePermissionGranted(context)

        try {
            FloatyService.addWindow(window)
            return true
        } catch (e: Exception) {
            e.printStackTrace()

            if (hasPermission) {
                FloatingPermission.manageDrawOverlays(context)
                ToastUtils.showShort("没有悬浮窗权限")
            }
        }
        return false
    }

    fun createFullWindowLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            getWindowType(),
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSPARENT,
        )
    }

    fun showRobot() {
        val robotWindow = createThumbWindow()
        addWindow(Utils.getApp(), robotWindow)
    }

    private fun createThumbWindow(): MusumeFloater {
        val window = screenColorPickerThumb?.get()
        if (window != null && FloatyService.isShowing(window)) {
            return window
        }
        return window ?: MusumeFloater().apply {
            screenColorPickerThumb = WeakReference(this)
        }
    }

    val isRobotShowing: Boolean
        get() = screenColorPickerThumb?.get() != null

    @Suppress("DEPRECATION")
    fun getWindowType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }
}