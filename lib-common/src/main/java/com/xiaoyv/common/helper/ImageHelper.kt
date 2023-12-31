package com.xiaoyv.common.helper

import android.graphics.BitmapFactory
import android.net.Uri
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Class: [ImageHelper]
 *
 * @author why
 * @since 12/31/23
 */
object ImageHelper {

    suspend fun compressImage(uri: Uri, maxByteSize: Long = MemoryConstants.MB.toLong()): String {
        return compressImage(UriUtils.uri2File(uri), maxByteSize)
    }

    suspend fun compressImage(file: File, maxByteSize: Long = MemoryConstants.MB.toLong()): String {
        return withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val tmpDir = PathUtils.getCachePathExternalFirst() + "/image"
            val tmp = tmpDir + "/${System.currentTimeMillis()}.png"
            val quality = ImageUtils.compressByQuality(bitmap, maxByteSize, true)
            FileUtils.deleteAllInDir(tmpDir)
            FileIOUtils.writeFileFromBytesByStream(tmp, quality)
            tmp
        }
    }
}