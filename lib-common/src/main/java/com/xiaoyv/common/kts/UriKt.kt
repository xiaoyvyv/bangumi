package com.xiaoyv.common.kts

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.blankj.utilcode.util.Utils
import java.io.InputStream

/**
 * 获取 Uri 文件名
 */
fun Uri.parseFileName(contentResolver: ContentResolver): String {
    var fileName: String? = null
    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                fileName = cursor.getString(displayNameIndex)
            }
        }
    }

    if (fileName == null) {
        // 从 URI 的路径中提取文件名
        val pathSegments = this.pathSegments
        if (pathSegments != null && pathSegments.isNotEmpty()) {
            fileName = pathSegments.last()
        }
    }

    return fileName.orEmpty()
}

fun Uri.inputStream(): InputStream {
    return requireNotNull(Utils.getApp().contentResolver.openInputStream(this))
}
