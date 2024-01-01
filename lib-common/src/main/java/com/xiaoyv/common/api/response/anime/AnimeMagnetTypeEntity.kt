package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [AnimeMagnetTypeEntity]
 *
 * @author why
 * @since 1/1/24
 */
@Keep
@Parcelize
data class AnimeMagnetTypeEntity(
    @SerializedName("Subgroups")
    var subgroups: List<Type>? = null,
    @SerializedName("Types")
    var types: List<Type>? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Type(
        @SerializedName("Id")
        var id: Int = 0,
        @SerializedName("Name")
        var name: String? = null,
    ) : Parcelable
}
