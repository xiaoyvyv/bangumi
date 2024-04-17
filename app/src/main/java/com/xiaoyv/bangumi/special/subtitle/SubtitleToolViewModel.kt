package com.xiaoyv.bangumi.special.subtitle

import android.net.Uri
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubtitleToolViewModel : BaseViewModel() {

    fun loadSubtitleFromMedia(uri: Uri) {
        launchUI(error = {
            it.printStackTrace()

        }) {
            withContext(Dispatchers.IO) {

            }
        }
    }


    /*
        fun loadSubtitleFile(uri: Uri) {
            launchUI(error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            }) {
                withContext(Dispatchers.IO) {
                    val fileName = uri.parseFileName(context.contentResolver)

                    debugLog { "文件名：$fileName" }

                    when {
                        fileName.endsWith(".ass", true) -> {
                            val parser = ASSParser()
                            val subtitle = uri.inputStream().use { parser.parse(it, fileName) }
                            debugLog { subtitle.toString() }

                            FileIOUtils.writeFileFromString(
                                PathUtils.getExternalAppFilesPath() + "/subtitle.ass",
                                subtitle.toString()
                            )
                        }

                        fileName.endsWith(".srt", true) -> {
                            val parser = SRTParser()
                            val subtitle = uri.inputStream().use { parser.parse(it, fileName) }
                            debugLog { subtitle.toString() }
                        }

                        else -> throw IllegalArgumentException("请选择 .ass 或 .srt 字幕文件")
                    }
                }
            }

        }*/
}