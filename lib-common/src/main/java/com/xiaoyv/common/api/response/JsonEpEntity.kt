package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EpCollectType
import kotlinx.parcelize.Parcelize


/**
 * Class: [JsonEpEntity]
 *
 * @author why
 * @since 12/24/23
 */
@Keep
@Parcelize
data class JsonEpEntity(
    @SerializedName("episode")
    var episode: Episode? = null,

    @EpCollectType @SerializedName("type")
    var type: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Episode(
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
        var ep: Long = 0,
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("name_cn")
        var nameCn: String? = null,
        @SerializedName("sort")
        var sort: Int = 0,
        @SerializedName("subject_id")
        var subjectId: Int = 0,
        @SerializedName("type")
        var type: Int = 0,
    ) : Parcelable
}
