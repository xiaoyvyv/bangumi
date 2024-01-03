package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [GithubPutEntity]
 *
 * @author why
 * @since 1/3/24
 */
@Keep
@Parcelize
data class GithubPutEntity(
    @SerializedName("content")
    var content: GithubContent? = null,
) : Parcelable