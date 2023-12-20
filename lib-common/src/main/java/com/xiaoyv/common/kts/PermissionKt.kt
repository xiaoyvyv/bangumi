package com.xiaoyv.common.kts

import android.Manifest
import com.blankj.utilcode.util.PermissionUtils
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * PermissionKt
 *
 * @author why
 * @since 11/19/23
 */
suspend fun requireStoragePermission(): Boolean {
    return suspendCancellableCoroutine {
        PermissionUtils.permission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                it.resumeWith(Result.success(true))
            }

            override fun onDenied() {
                it.resumeWith(Result.success(false))
            }
        }).request()
    }
}