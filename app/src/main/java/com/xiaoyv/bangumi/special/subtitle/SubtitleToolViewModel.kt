package com.xiaoyv.bangumi.special.subtitle

import android.net.Uri
import androidx.media3.common.DataReader
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.DefaultExtractorInput
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ExtractorOutput
import androidx.media3.extractor.PositionHolder
import androidx.media3.extractor.mp4.Mp4Extractor
import androidx.media3.extractor.text.DefaultSubtitleParserFactory
import androidx.media3.extractor.text.SubtitleParser
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.inputStream
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@UnstableApi
class SubtitleToolViewModel : BaseViewModel() {

    fun loadSubtitleFromMedia(uri: Uri) {
        launchUI(error = {
            it.printStackTrace()

        }) {
            // SubtitleExtractor
            withContext(Dispatchers.IO) {
                val parserFactory = DefaultSubtitleParserFactory()
                val create = parserFactory.create(
                    Format.Builder()
                        .setSampleMimeType(MimeTypes.TEXT_SSA)
                        .build()
                )
                create.parse(uri.inputStream().readBytes(), SubtitleParser.OutputOptions.allCues()) {

                    debugLog { it.toJson() }
                }
//
//                val extractors = DefaultExtractorsFactory()
//                    .setSubtitleParserFactory(parserFactory)
//                    .createExtractors(uri, emptyMap())
//                extractors.first()
//                val mp4Extractor = Mp4Extractor(parserFactory)
//                mp4Extractor.init(ExtractorOutput.PLACEHOLDER)
//                mp4Extractor.read(DefaultExtractorInput(DataReader), PositionHolder())
//                mp4Extractor.getSeekPoints()
//                debugLog { "Extractors: ${extractors.size}" }
            }
        }
    }

    fun loadSubtitleFile(uri: Uri) {
        launchUI(error = {
            it.printStackTrace()

            showToastCompat(it.errorMsg)
        }) {
            /* withContext(Dispatchers.IO) {
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
             }*/
        }

    }
}