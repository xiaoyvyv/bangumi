package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Immutable
@Serializable
data class ComposeMono(
    @SerialName("comment") val comment: Int = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("images") val images: ComposeImages = ComposeImages.Empty,
    @SerialName("info") val info: String = "",
    @SerialName("lock") val lock: Boolean = false,
    @SerialName("name") val name: String = "",
    @SerialName("nameCN") val nameCN: String = "",
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("collectedAt") val collectedAt: SerializeDateLong = 0,
    @SerialName("collects") val collects: Int = 0,
    @SerialName("redirect") val redirect: Int = 0,
    @SerialName("summary") val summary: String = "",

    /**
     * Detail info
     */
    @SerialName("infobox") val infobox: List<ComposeInfobox> = listOf(),

    /**
     * Only for person
     */
    @SerialName("career") val career: JsonElement = JsonObject(emptyMap()),
    @SerialName("type") val type: Int = 0,

    /**
     * Only for character
     */
    @SerialName("role") val role: Int = 0,

    /**
     * 本地内容扩展
     */
    @SerialName("webInfo")
    val webInfo: ComposeMonoWebInfo = ComposeMonoWebInfo.Empty,
) {
    val displayName: String get() = nameCN.ifBlank { name }

    fun shareUrl(@MonoType type: Int): String {
        return if (type == MonoType.PERSON) {
            WebConstant.URL_BASE_WEB + "person/$id"
        } else {
            WebConstant.URL_BASE_WEB + "character/$id"
        }
    }

    companion object {
        val Empty = ComposeMono()
    }
}
