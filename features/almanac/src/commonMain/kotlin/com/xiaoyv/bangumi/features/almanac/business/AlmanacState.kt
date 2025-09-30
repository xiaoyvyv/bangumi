package com.xiaoyv.bangumi.features.almanac.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf

/**
 * [AlmanacState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class AlmanacState(
    val bgmHost: String = "",
    val almanacs: SerializeList<Pair<Int, String>> = persistentListOf(),
)
