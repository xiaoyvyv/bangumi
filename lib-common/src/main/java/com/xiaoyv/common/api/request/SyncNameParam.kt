package com.xiaoyv.common.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [SyncNameParam]
 *
 * @author why
 * @since 1/27/24
 */
@Parcelize
data class SyncNameParam(
    @SerializedName("id") var id: String = "",
    @SerializedName("name") var name: String? = null,
    @SerializedName("type") var type: Int? = null,
) : Parcelable