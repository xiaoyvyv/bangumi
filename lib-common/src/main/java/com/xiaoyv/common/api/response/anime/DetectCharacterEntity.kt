package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File


/**
 * Class: [DetectCharacterEntity]
 *
 * @author why
 * @since 11/21/23
 */
@Parcelize
@Keep
data class DetectCharacterEntity(
    @SerializedName("box")
    var box: List<Double>? = null,
    @SerializedName("box_id")
    var boxId: String? = null,
    @SerializedName("char")
    var char: List<Character>? = null,
    @SerializedName("file")
    var imageFile: File?=null
) : Parcelable {

    @Parcelize
    @Keep
    data class Character(
        @SerializedName("acc")
        var acc: Double = 0.0,
        @SerializedName("cartoonname")
        var cartoonname: String? = null,
        @SerializedName("name")
        var name: String? = null
    ) : Parcelable
}