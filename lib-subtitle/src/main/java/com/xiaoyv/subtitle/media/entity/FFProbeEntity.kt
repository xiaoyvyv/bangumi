@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.subtitle.media.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class FFProbeEntity(
    @SerializedName("format") var format: Format? = null,
    @SerializedName("streams") var streams: List<Stream>? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class Format(
        @SerializedName("bit_rate") var bitRate: String? = null,
        @SerializedName("duration") var duration: String? = null,
        @SerializedName("filename") var filename: String? = null,
        @SerializedName("format_long_name") var formatLongName: String? = null,
        @SerializedName("format_name") var formatName: String? = null,
        @SerializedName("nb_programs") var nbPrograms: Int = 0,
        @SerializedName("nb_streams") var nbStreams: Int = 0,
        @SerializedName("probe_score") var probeScore: Double = 0.0,
        @SerializedName("size") var size: String? = null,
        @SerializedName("start_time") var startTime: String? = null,
        @SerializedName("tags") var tags: Tags? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Stream(
        @SerializedName("avg_frame_rate") var avgFrameRate: String? = "",
        @SerializedName("codec_long_name") var codecLongName: String? = "",
        @SerializedName("codec_name") var codecName: String? = "",
        @SerializedName("codec_tag") var codecTag: String? = "",
        @SerializedName("codec_tag_string") var codecTagString: String? = "",
        @SerializedName("codec_type") var codecType: String? = "",
        @SerializedName("disposition") var disposition: Disposition? = null,
        @SerializedName("duration") var duration: String? = "",
        @SerializedName("duration_ts") var durationTs: Long = 0,
        @SerializedName("extradata_size") var extradataSize: Long = 0,
        @SerializedName("index") var index: Int = 0,
        @SerializedName("r_frame_rate") var rFrameRate: String? = "",
        @SerializedName("start_pts") var startPts: Long = 0,
        @SerializedName("start_time") var startTime: String? = "",
        @SerializedName("tags") var tags: Tags? = Tags(),
        @SerializedName("time_base") var timeBase: String? = ""
    ) : Parcelable {
        /**
         * 标题
         */
        val displayTitle: String
            get() {
                val defaultName = "$codecType-$index.$codecName"
                val tagInfo = tags ?: return defaultName
                val title = tagInfo.title.orEmpty()
                val filename = tagInfo.filename.orEmpty()
                val language = tagInfo.language.orEmpty()
                return title.ifBlank { filename }.ifBlank { language }.ifBlank { defaultName }
            }

        /**
         * 标题带文件格式扩展名
         */
        val displayFileName: String
            get() {
                val code = codecName.orEmpty()
                val title = displayTitle
                if (code.isBlank() || title.endsWith(code)) return title.legalizeFileName()
                return "$title.$code".legalizeFileName()
            }

        /**
         * 文件名合法化
         */
        private fun String.legalizeFileName(): String {
            val illegalCharsRegex = "[\\\\/:*?\"<>|]".toRegex()
            return replace(illegalCharsRegex, "-")
        }
    }

    @Keep
    @Parcelize
    data class Disposition(
        @SerializedName("attached_pic") var attachedPic: Int = 0,
        @SerializedName("captions") var captions: Int = 0,
        @SerializedName("clean_effects") var cleanEffects: Int = 0,
        @SerializedName("comment") var comment: Int = 0,
        @SerializedName("default") var default: Int = 0,
        @SerializedName("dependent") var dependent: Int = 0,
        @SerializedName("descriptions") var descriptions: Int = 0,
        @SerializedName("dub") var dub: Int = 0,
        @SerializedName("forced") var forced: Int = 0,
        @SerializedName("hearing_impaired") var hearingImpaired: Int = 0,
        @SerializedName("karaoke") var karaoke: Int = 0,
        @SerializedName("lyrics") var lyrics: Int = 0,
        @SerializedName("metadata") var metadata: Int = 0,
        @SerializedName("non_diegetic") var nonDiegetic: Int = 0,
        @SerializedName("original") var original: Int = 0,
        @SerializedName("still_image") var stillImage: Int = 0,
        @SerializedName("timed_thumbnails") var timedThumbnails: Int = 0,
        @SerializedName("visual_impaired") var visualImpaired: Int = 0
    ) : Parcelable

    @Keep
    @Parcelize
    data class Tags(
        @SerializedName("title") var title: String? = null,
        @SerializedName("filename") var filename: String? = null,
        @SerializedName("language") var language: String? = null,
        @SerializedName("encoder") var encoder: String? = null,
        @SerializedName("mimetype") var mimetype: String? = null,
    ) : Parcelable
}