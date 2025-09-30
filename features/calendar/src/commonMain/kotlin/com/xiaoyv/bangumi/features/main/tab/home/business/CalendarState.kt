package com.xiaoyv.bangumi.features.main.tab.home.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [CalendarState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class CalendarState(
    @SerialName("isToday") val isToday: Boolean = true,
    @SerialName("isGrid") val isGrid: Boolean = false,
    @SerialName("calendarMap") val calendarMap: SerializeMap<String, List<ComposeHomeSection>> = persistentMapOf(),
)
