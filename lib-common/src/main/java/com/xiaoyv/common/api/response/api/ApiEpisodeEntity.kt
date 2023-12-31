package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.blankj.utilcode.util.TimeUtils
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpApiType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ApiEpisodeEntity(
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("airdate")
    var airdate: String? = null,
    @SerializedName("comment")
    var comment: Int = 0,
    @SerializedName("desc")
    var desc: String? = null,
    @SerializedName("disc")
    var disc: Int = 0,
    @SerializedName("duration")
    var duration: String? = null,
    @SerializedName("duration_seconds")
    var durationSeconds: Long = 0,
    @SerializedName("ep")
    var ep: String = "",
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("name_cn")
    var nameCn: String? = null,
    @SerializedName("sort")
    var sort: Double = 0.0,
    @SerializedName("subject_id")
    var subjectId: Long = 0,
    @SerializedName("type")
    @EpApiType var type: Int = 0,
    @SerializedName("infoState")
    var infoState: InfoState = InfoState(),
) : Parcelable {

    val epText: String
        get() = if (type == EpApiType.TYPE_MAIN) ep
        else sort.toString().removeSuffix(".0")

    /**
     * 解析放送状态
     */
    fun fillState(
        @EpCollectType collectType: Int,
        @MediaType mediaType: String = MediaType.TYPE_UNKNOWN,
    ): ApiEpisodeEntity {
        val info = InfoState()
        val insType = EpCollectType.toInterestType(collectType)

        info.collectStateText = InterestType.string(insType, mediaType)

        // 放送时间
        val millis = airDateTimestamp()
        info.chineseWeek = if (millis == Long.MAX_VALUE) "" else TimeUtils.getChineseWeek(millis)

        // 解析放送状态，是否放映中
        if (TimeUtils.isToday(millis)) {
            info.isAiring = true
            info.isAired = false
            info.airedStateText = "放送中"
        } else {
            info.isAiring = false
            info.isAired = System.currentTimeMillis() > millis
            info.airedStateText = if (info.isAired) "已放送" else "未放送"
        }

        infoState = info
        return this
    }

    /**
     * 放映时间
     */
    private fun airDateTimestamp(): Long {
        return when (airdate.orEmpty().length) {
            4 -> TimeUtils.string2Millis(airdate.orEmpty(), "yyyy")
            7 -> TimeUtils.string2Millis(airdate.orEmpty(), "yyyy-MM")
            10 -> TimeUtils.string2Millis(airdate.orEmpty(), "yyyy-MM-dd")
            else -> Long.MAX_VALUE
        }
    }

    @Keep
    @Parcelize
    data class InfoState(
        var airedStateText: String = "",
        var isAired: Boolean = false,
        var isAiring: Boolean = false,
        var collectStateText: String = "",
        var chineseWeek: String = "",
    ) : Parcelable
}