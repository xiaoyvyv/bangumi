package com.xiaoyv.bangumi.shared.data.model.request.list.album

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListAlbumParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListAlbumParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListAlbumType
    @SerialName("type")
    val type: Int = ListAlbumType.UNKNOWN,

    /**
     * [ListAlbumType.CHARACTER_ALBUM]
     */
    @SerialName("characterId") val characterId: Long = 0,

    /**
     * [ListAlbumType.SUBJECT_PREVIEW]
     */
    @SerialName("doubanId") val doubanId: String = "",
    @SerialName("doubanType") val doubanType: String = "",
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListAlbumParam()
    }
}