package com.xiaoyv.bangumi.ui.feature.floater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.blankj.utilcode.util.ToastUtils;
import com.xiaoyv.floater.util.FloatingWindowPermissionUtil;

/**
 * Created by Stardust on 2018/1/30.
 */
public class FloatingPermission {

    public static boolean ensurePermissionGranted(Context context) {
        if (!canDrawOverlays(context)) {
            ToastUtils.showShort("没有悬浮窗权限");
            manageDrawOverlays(context);
            return false;
        }
        return true;
    }

    public static void manageDrawOverlays(Context context) {
        try {
            manageDrawOverlaysForAndroidM(context);
        } catch (Exception ex) {
            FloatingWindowPermissionUtil.goToAppDetailSettings(context, context.getPackageName());
        }
    }

    public static void manageDrawOverlaysForAndroidM(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static boolean canDrawOverlays(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
