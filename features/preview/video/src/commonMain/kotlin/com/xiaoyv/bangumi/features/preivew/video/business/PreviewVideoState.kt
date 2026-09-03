package com.xiaoyv.bangumi.features.preivew.video.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * [PreviewVideoState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PreviewVideoState(
    val url: String,
    val headers: SerializeMap<String, String> = persistentMapOf(),
)
