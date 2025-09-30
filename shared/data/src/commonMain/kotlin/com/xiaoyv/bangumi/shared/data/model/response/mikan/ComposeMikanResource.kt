package com.xiaoyv.bangumi.shared.data.model.response.mikan

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


private val regex = "\\[(.*?)]".toRegex()

/**
 * [ComposeMikanResource]
 *
 * @author why
 * @since 2025/1/23
 */
@Immutable
@Serializable
data class ComposeMikanResource(
    @SerialName("fileSize") val fileSize: String = "",
    @SerialName("magnet") val magnet: String = "",
    @SerialName("pageUrl") val pageUrl: String = "",
    @SerialName("publishDate") val publishDate: String = "",
    @SerialName("subgroupId") val subgroupId: Long = 0,
    @SerialName("subgroupName") val subgroupName: String = "",
    @SerialName("title") val title: String = "",
    @Transient val titleHtml: AnnotatedString = AnnotatedString(""),
    @SerialName("typeId") val typeId: Int = 0,
    @SerialName("typeName") val typeName: String = "",
    @SerialName("torrent") val torrent: String = "",
) {

    val tags by lazy {
        regex.findAll(title)
            .map { it.groupValues[1].parseAsHtml() }
            .toList()
    }
}