package com.xiaoyv.subtitle.media

import android.content.Context
import android.net.Uri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import com.google.gson.Gson
import com.xiaoyv.subtitle.media.entity.FFProbeEntity
import java.io.File

class MediaSubtitleExtractor private constructor(
    private val context: Context,
    private val uri: Uri
) {
    private val gson by lazy { Gson() }

    /**
     * 获取流信息
     */
    @Throws(IllegalStateException::class)
    fun extractStreamInfo(): FFProbeEntity {
        val input = FFmpegKitConfig.getSafParameterForRead(context, uri)
        val session =
            FFprobeKit.execute("-v error -select_streams s -show_entries stream:format -print_format json \"$input\"")

        check(session.returnCode.isValueSuccess) {
            session.failStackTrace
        }

        try {
            return gson.fromJson(session.output.orEmpty(), FFProbeEntity::class.java)
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * 抽取字幕信息，返回字幕文件
     */
    @Throws(IllegalStateException::class)
    fun extractSubtitle(stream: FFProbeEntity.Stream, saveDir: String): File {
        val input = FFmpegKitConfig.getSafParameterForRead(context, uri)
        val saveDirFile = File(saveDir)
        if (saveDirFile.exists().not()) saveDirFile.mkdirs()
        val saveFile = File(saveDir + File.separator + stream.displayFileName)
        if (saveFile.exists()) {
            if (saveFile.isDirectory) saveFile.deleteRecursively()
            saveFile.delete()
        }
        val session = FFmpegKit.execute(
            "-v error -y -i \"$input\" -map 0:${stream.index} -c copy \"${saveFile.absolutePath}\""
        )

        check(session.returnCode.isValueSuccess) {
            session.failStackTrace
        }

        return saveFile
    }

    companion object {
        fun from(context: Context, uri: Uri): MediaSubtitleExtractor {
            return MediaSubtitleExtractor(context, uri)
        }
    }
}