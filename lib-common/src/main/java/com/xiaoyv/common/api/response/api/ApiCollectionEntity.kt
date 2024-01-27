package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [ApiCollectionEntity]
 *
 * @author why
 * @since 1/26/24
 */
@Keep
@Parcelize
data class ApiCollectionEntity(
    @SerializedName("data")
    var `data`: List<Data>? = null,
    @SerializedName("limit")
    var limit: Int = 0,
    @SerializedName("offset")
    var offset: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Data(
        @SerializedName("comment")
        var comment: String? = null,
        @SerializedName("ep_status")
        var epStatus: Int = 0,
        @SerializedName("private")
        var `private`: Boolean = false,
        @SerializedName("rate")
        var rate: Int = 0,
        @SerializedName("subject")
        var subject: ApiSubjectEntity? = null,
        @SerializedName("subject_id")
        var subjectId: Int = 0,
        @SerializedName("subject_type")
        var subjectType: Int = 0,
        @SerializedName("tags")
        var tags: List<String>? = null,
        @SerializedName("type")
        var type: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String? = null,
        @SerializedName("vol_status")
        var volStatus: Int = 0,
    ) : Parcelable
}