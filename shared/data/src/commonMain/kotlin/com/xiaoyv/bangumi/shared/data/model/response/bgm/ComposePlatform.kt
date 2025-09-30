package com.xiaoyv.bangumi.shared.data.model.response.bgm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ComposePlatform(
    @SerialName("alias") val alias: String = "",
    @SerialName("enableHeader") val enableHeader: Boolean = false,
    @SerialName("id") val id: Long = 0,
    @SerialName("order") val order: Int = 0,
    @SerialName("type") val type: String = "",
    @SerialName("typeCN") val typeCN: String = "",
    @SerialName("wikiTpl") val wikiTpl: String = "",
) {
    companion object {
        fun fromString(platform: String): ComposePlatform {
            return ComposePlatform(
                typeCN = platform,
                type = platform,
                alias = platform,
            )
        }

        val Empty = ComposePlatform()
    }
}