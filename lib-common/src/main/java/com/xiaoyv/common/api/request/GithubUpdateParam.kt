package com.xiaoyv.common.api.request

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [GithubUpdateParam]
 *
 * @author why
 * @since 1/3/24
 */
@Keep
@Parcelize
data class GithubUpdateParam(
    @SerializedName("committer")
    var committer: Committer? = null,
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("sha")
    var sha: String? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Committer(
        @SerializedName("email")
        var email: String? = null,
        @SerializedName("name")
        var name: String? = null,
    ) : Parcelable
}