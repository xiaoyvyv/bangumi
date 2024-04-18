package com.xiaoyv.bangumi.special.subtitle

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.PathUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.request.MicrosoftTranslateParam
import com.xiaoyv.common.config.annotation.SubtitleActionType
import com.xiaoyv.common.config.bean.SubtitleResult
import com.xiaoyv.common.helper.UserTokenHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.subtitle.api.parser.ParserFactory
import com.xiaoyv.subtitle.api.subtitle.common.SubtitleLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.media.MediaSubtitleExtractor
import com.xiaoyv.subtitle.media.entity.FFProbeEntity
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.sendValue
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class SubtitleToolViewModel : BaseViewModel() {
    internal val onStreamInfoLiveData = MutableLiveData<List<FFProbeEntity.Stream>?>()
    internal val onSubtitleLiveData = MutableLiveData<SubtitleResult?>()
    internal val onTranslateProgress = MutableLiveData<Pair<Int, Int>>()

    private var extractor: MediaSubtitleExtractor? = null

    /**
     * 翻译时，剔除样式数据
     */
    private val regex = "\\{[\\s\\S]+?\\}"

    fun loadMediaStreamInfo(uri: Uri) {
        launchUI(
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)

                onStreamInfoLiveData.value = null
            },
            block = {
                onStreamInfoLiveData.value = withContext(Dispatchers.IO) {
                    extractor = MediaSubtitleExtractor.from(context, uri)
                    val entity = extractor?.extractStreamInfo()
                    val streams = entity?.streams.orEmpty()
                    check(streams.isNotEmpty()) { "当前文件没有内挂任何字幕" }
                    streams
                }
            }
        )
    }

    /**
     * 抽取字幕并解析
     */
    fun extractSubtitle(@SubtitleActionType action: Int, stream: FFProbeEntity.Stream) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)

                onSubtitleLiveData.value = null
            },
            block = {
                val subtitleExtractor = requireNotNull(extractor)
                val saveDir = PathUtils.getFilesPathExternalFirst() + "/subtitle"

                onSubtitleLiveData.value = withContext(Dispatchers.IO) {
                    val subtitle = subtitleExtractor.extractSubtitle(stream, saveDir)

                    SubtitleResult(
                        file = subtitle,
                        timedTextFile = ParserFactory
                            .getParser(subtitle.extension)
                            .parse(subtitle),
                        actionType = action
                    )
                }
            }
        )
    }

    /**
     * 翻译字幕
     */
    fun translateSubtitle(result: SubtitleResult) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)

                onSubtitleLiveData.value = null
            },
            block = {
                withContext(Dispatchers.IO) {
                    val subtitle = result.timedTextFile
                    val needTranslateTimedLine = arrayListOf<TimedLine>()
                    // 遍历每一个字幕帧
                    subtitle.getTimedLines().forEach {
                        needTranslateTimedLine.add(it)
                    }

                    // 分组并发翻译
                    val total = AtomicInteger(needTranslateTimedLine.size)
                    val current = AtomicInteger(0)
                    val group = 10
                    val chunkedCount = (total.get() / group).let { if (it == 0) 1 else it }
                    val tasks = needTranslateTimedLine.chunked(chunkedCount).map {
                        async(Dispatchers.IO) {
                            it.forEach { item ->
                                runCatching { translateSubtitleTimeline(item) }
                                onTranslateProgress.sendValue(current.incrementAndGet() to total.get())
                            }
                        }
                    }
                    awaitAll(*tasks.toTypedArray())

                    // 写入文件
                    val translateFile = File(
                        result.file.parent,
                        result.file.nameWithoutExtension + "-zh-CN." + result.file.extension
                    )
                    FileIOUtils.writeFileFromString(translateFile, subtitle.toString())

                    debugLog { "翻译完成！$translateFile" }
                }
            }
        )
    }

    /**
     * 翻译一个字幕帧
     */
    private suspend fun translateSubtitleTimeline(timedLine: TimedLine) {
        if (timedLine !is SubtitleLine<*>) return

        // 原文为 KEY，译文为 VALUE
        val originalToTranslateMap: MutableMap<String, String> = timedLine.getTextLines()
            .associate { it.replace(regex.toRegex(), "") to "" }
            .toMutableMap()

        // 原文
        val original = originalToTranslateMap.keys.toList()

        // 译文，顺序和原文一一对应
        val translateResult = BgmApiManager.bgmJsonApi.postMicrosoftTranslate(
            authentication = "Bearer ${UserTokenHelper.queryMicrosoftToken()}",
            param = original.map { MicrosoftTranslateParam(text = it) }
        ).map { it.translations?.firstOrNull()?.text.orEmpty() }

        // 更新 MAP 映射表的译文
        original.forEach {
            val originalIndex = original.indexOf(it)
            originalToTranslateMap[it] = translateResult[originalIndex]
        }

        // 将翻译结果一一替换
        val newTextLines = timedLine.getTextLines().map {
            var result = it
            originalToTranslateMap.forEach { (key, value) ->
                if (it.contains(key)) {
                    result = it.replace(key, value)
                }
            }
            result
        }

        // 设置翻译后的数据
        timedLine.setTextLines(ArrayList(newTextLines))
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