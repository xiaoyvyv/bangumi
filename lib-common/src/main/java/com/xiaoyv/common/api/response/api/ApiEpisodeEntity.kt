package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpApiType
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
    var ep: Int = 0,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("name_cn")
    var nameCn: String? = null,
    @SerializedName("sort")
    var sort: Int = 0,
    @SerializedName("subject_id")
    var subjectId: Long = 0,
    @SerializedName("type")
    @EpApiType var type: Int = 0,
) : Parcelable