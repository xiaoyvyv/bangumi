package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [GithubContent]
 *
 * @author why
 * @since 1/3/24
 */
@Keep
@Parcelize
data class GithubContent(
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("download_url")
    var downloadUrl: String? = null,
    @SerializedName("encoding")
    var encoding: String? = null,
    @SerializedName("git_url")
    var gitUrl: String? = null,
    @SerializedName("html_url")
    var htmlUrl: String? = null,
    @SerializedName("_links")
    var links: Links? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("path")
    var path: String? = null,
    @SerializedName("sha")
    var sha: String? = null,
    @SerializedName("size")
    var size: Long = 0,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("url")
    var url: String? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Links(
        @SerializedName("git")
        var git: String? = null,
        @SerializedName("html")
        var html: String? = null,
        @SerializedName("self")
        var self: String? = null,
    ) : Parcelable
}