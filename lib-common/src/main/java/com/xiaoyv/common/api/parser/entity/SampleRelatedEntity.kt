package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [SampleRelatedEntity]
 *
 * @author why
 * @since 12/13/23
 */
@Keep
@Parcelize
data class SampleRelatedEntity(
    @SerializedName("title") var title: String = "",
    @SerializedName("items") var items: MutableList<Item> = mutableListOf(),
) : Parcelable {

    @Keep
    @Parcelize
    data class Item(
        @SerializedName("image") var image: String = "",
        @SerializedName("imageLink") var imageLink: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("titleLink") var titleLink: String = ""
    ) : Parcelable
}
