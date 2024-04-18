package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.config.annotation.SubtitleActionType
import com.xiaoyv.subtitle.api.subtitle.common.TimedLine
import com.xiaoyv.subtitle.api.subtitle.common.TimedTextFile
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class SubtitleResult(
    var file: File,
    var timedTextFile: TimedTextFile<out TimedLine>,
    @SubtitleActionType var actionType: Int
) : Parcelable
