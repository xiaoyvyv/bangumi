package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param name
 * @param translatedName
 */
@Serializable

data class IllustrationTag(

    @SerialName(value = "name")
    val name: String = "",

    @SerialName(value = "translated_name")
    val translatedName: String = ""

)

