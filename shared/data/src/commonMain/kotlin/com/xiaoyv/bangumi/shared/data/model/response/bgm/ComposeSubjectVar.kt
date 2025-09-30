package com.xiaoyv.bangumi.shared.data.model.response.bgm

import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeSubjectVariant(
    @SerialName("collection") val collection: ComposeCollectionInfo = ComposeCollectionInfo.Empty,
    @SerialName("date") val date: String = "",
    @SerialName("eps") val eps: Int = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("image") val image: String = "",
    @SerialName("images") val images: ComposeImages = ComposeImages.Empty,
    @SerialName("infobox") val infobox: SerializeList<ComposeInfobox> = persistentListOf(),
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("meta_tags") val metaTags: SerializeList<String> = persistentListOf(),
    @SerialName("name") val name: String = "",
    @SerialName("name_cn") val nameCn: String = "",
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("platform") val platform: String = "",
    @SerialName("rating") val rating: ComposeRating = ComposeRating.Empty,
    @SerialName("series") val series: Boolean = false,
    @SerialName("summary") val summary: String = "",
    @SerialName("tags") val tags: SerializeList<ComposeTag> = persistentListOf(),
    @SerialName("type") val type: Int = 0,
    @SerialName("volumes") val volumes: Int = 0,
) {
    fun toSubject(): ComposeSubject {
        val airtimeValue = if (date.isNotBlank()) Airtime.parse(date) else Airtime.Empty

        // platform 字符串转换为 ComposePlatform
        val platformValue = if (platform.isNotBlank()) {
            ComposePlatform.fromString(platform)
        } else {
            ComposePlatform.Empty
        }

        // info 可以用 summary 或者 infobox 拼接生成
        val infoValue = buildString {
            if (summary.isNotBlank()) append(summary)
            if (infobox.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                append(infobox.joinToString("\n") { "${it.key}: ${it.value}" })
            }
        }

        return ComposeSubject(
            airtime = airtimeValue,
            collection = collection,
            eps = eps,
            volumes = volumes,
            id = id,
            images = images,
            infobox = infobox,
            locked = locked,
            metaTags = metaTags,
            name = name,
            nameCn = nameCn,
            nsfw = nsfw,
            platform = platformValue,
            rating = rating,
            series = series,
            summary = summary,
            info = infoValue,
            tags = tags,
            type = type,
            seriesEntry = 0, // Variant 没有 seriesEntry，可默认 0
            redirect = 0, // Variant 没有 redirect
            addedAt = "", // Variant 没有 addedAt
            comment = "", // Variant 没有 comment
            relation = "", // Variant 没有 relation
            interest = ComposeSubjectInterest.Empty, // Variant 没有 interest
            webInfo = ComposeSubjectWebInfo.Empty,
        )
    }
}
