package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.shared.core.types.CareerType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,
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
    @SerialName("type") val type: Int = 0,

    /**
     * Only for person
     *
     * value: "seiyu" or ["seiyu","artist"]
     */
    @SerialName("career")
    val career: SerializeList<String> = persistentListOf(),

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
    val displayName = nameCN.ifBlank { name }

    /**
     * 职业
     */
    val displayCareer = career
        .mapNotNull { CareerType.string(it).takeIf { resource -> resource != Res.string.global_unknown } }
        .toImmutableList()

    fun shareUrl(@MonoType type: Int): String {
        return if (type == MonoType.PERSON) {
            WebConstant.URL_BASE_WEB + "person/$id"
        } else {
            WebConstant.URL_BASE_WEB + "character/$id"
        }
    }

    fun normalized(): ComposeMono {
        return copy(summary = summary.bbcodeToHtml())
    }

    companion object {
        val Empty = ComposeMono()
    }
}
