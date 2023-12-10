package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [BaiduTranslateEntity]
 *
 * @author why
 * @since 12/10/23
 */
@Keep
@Parcelize
data class BaiduTranslateEntity(
    @SerializedName("from")
    var from: String? = null,
    @SerializedName("to")
    var to: String? = null,
    @SerializedName("trans_result")
    var transResult: List<TransResult>? = null,
    @SerializedName("error_code")
    var errorCode: String? = null,
    @SerializedName("error_msg")
    var errorMsg: String? = null
) : Parcelable {
    @Keep
    @Parcelize
    data class TransResult(
        @SerializedName("dst")
        var dst: String? = null,
        @SerializedName("src")
        var src: String? = null
    ) : Parcelable
}