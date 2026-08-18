package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param name
 * @param translatedName
 * @param addedByUploadedUser
 */
@Serializable

data class NovelTag(

    @SerialName(value = "name")
    val name: String = "",

    @SerialName(value = "translated_name")
    val translatedName: String = "",

    @SerialName(value = "added_by_uploaded_user")
    val addedByUploadedUser: Boolean? = null

)

