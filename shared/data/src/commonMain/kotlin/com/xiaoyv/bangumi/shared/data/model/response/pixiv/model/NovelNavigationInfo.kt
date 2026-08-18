package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param viewable
 * @param contentOrder
 * @param title
 * @param coverUrl
 * @param viewableMessage
 */
@Serializable

data class NovelNavigationInfo(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "viewable")
    val viewable: Boolean? = null,

    @SerialName(value = "content_order")
    val contentOrder: String = "",

    @SerialName(value = "title")
    val title: String = "",

    @SerialName(value = "cover_url")
    val coverUrl: String = "",

    @SerialName(value = "viewable_message")
    val viewableMessage: String = ""

)

