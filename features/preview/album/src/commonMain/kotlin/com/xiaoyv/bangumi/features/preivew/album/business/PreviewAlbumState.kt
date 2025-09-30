package com.xiaoyv.bangumi.features.preivew.album.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import kotlinx.serialization.SerialName

/**
 * [PreviewAlbumState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PreviewAlbumState(
    @field:ListAlbumType
    @SerialName("type")
    val type: Int = ListAlbumType.UNKNOWN,
)
