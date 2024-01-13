package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


/**
 * Class: [ApiCharacterEntity]
 *
 * @author why
 * @since 1/13/24
 */
@Keep
@Parcelize
data class ApiCharacterEntity(
    @SerializedName("birth_day")
    var birthDay: Int = 0,
    @SerializedName("birth_mon")
    var birthMon: Int = 0,
    @SerializedName("birth_year")
    var birthYear: Int = 0,
    @SerializedName("blood_type")
    var bloodType: Int = 0,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("images")
    var images: Images? = null,
    @SerializedName("infobox")
    var infobox: List<Infobox>? = null,
    @SerializedName("locked")
    var locked: Boolean = false,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("stat")
    var stat: Stat? = null,
    @SerializedName("summary")
    var summary: String? = null,
    @SerializedName("type")
    var type: Int = 0,
) : Parcelable {

    @Keep
    @Parcelize
    data class Infobox(
        @SerializedName("key")
        var key: String = "",
        @SerializedName("value")
        var value: @RawValue Any? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Images(
        @SerializedName("grid")
        var grid: String? = null,
        @SerializedName("large")
        var large: String? = null,
        @SerializedName("medium")
        var medium: String? = null,
        @SerializedName("small")
        var small: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Stat(
        @SerializedName("collects")
        var collects: Int = 0,
        @SerializedName("comments")
        var comments: Int = 0,
    ) : Parcelable
}