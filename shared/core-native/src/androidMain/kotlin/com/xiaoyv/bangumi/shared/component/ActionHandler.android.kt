package com.xiaoyv.bangumi.shared.component

import android.app.WallpaperManager
import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.core.content.FileProvider
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
actual fun rememberActionHandler(): ActionHandler {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    return remember(uriHandler, clipboard, context) {
        ActionHandler(context, uriHandler, clipboard)
    }
}


@Stable
actual class ActionHandler actual constructor(
    val uriHandler: UriHandler,
    val clipboard: Clipboard?,
) : CoroutineScope by MainScope() {
    private var context: Context? = null

    constructor(context: Context?, uriHandler: UriHandler, clipboard: Clipboard) : this(uriHandler, clipboard) {
        this.context = context
    }

    actual fun shareContent(content: String) {
        val ctx = context ?: return
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
            }
            val chooser = Intent.createChooser(intent, "分享")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun openInBrowser(link: String) {
        runCatching { uriHandler.openUri(link) }
    }


    actual fun copyContent(content: String) {
        launch {
            runCatching {
                clipboard?.setClipEntry(ClipEntry(ClipData.newPlainText("Share", content)))
            }
        }
    }

    actual fun saveMedia(file: PlatformFile) {
        launch {
            val ctx = context ?: return@launch
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        ctx,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        Toast.makeText(ctx, "未授予存储写入权限，无法保存图片", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                val bytes = file.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@launch
                val filename = "saved_image_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Bangumi")
                    }
                }
                val resolver = ctx.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    Toast.makeText(ctx, "保存成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "保存失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(ctx, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    actual fun shareMedia(file: PlatformFile) {
        launch {
            val ctx = context ?: return@launch
            try {
                val bytes = file.readBytes()
                val tempFile = java.io.File(ctx.cacheDir, "share_image_${System.currentTimeMillis()}.jpg")
                tempFile.writeBytes(bytes)

                val authority = "${ctx.packageName}.fileprovider"
                val uri = FileProvider.getUriForFile(ctx, authority, tempFile)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(intent, "分享图片")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.SET_WALLPAPER)
    actual fun setWallpaper(file: PlatformFile) {
        launch {
            val ctx = context ?: return@launch
            try {
                val bytes = file.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@launch
                val wallpaperManager = WallpaperManager.getInstance(ctx)
                wallpaperManager.setBitmap(bitmap)
                Toast.makeText(ctx, "壁纸设置成功", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
