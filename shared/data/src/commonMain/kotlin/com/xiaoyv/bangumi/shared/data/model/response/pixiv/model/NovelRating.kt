package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param like
 * @param bookmark
 * @param view
 */
@Serializable

data class NovelRating(

    @SerialName(value = "like")
    val like: Int = 0,

    @SerialName(value = "bookmark")
    val bookmark: Int = 0,

    @SerialName(value = "view")
    val view: Int = 0

)

