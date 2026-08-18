package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param title
 */
@Serializable

data class Series(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "title")
    val title: String = ""

)

