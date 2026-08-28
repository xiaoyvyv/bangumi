package com.xiaoyv.bangumi.features.main.tab.tracking.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.home.ComposeHomeProgress
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TrackingState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TrackingState(
    val tabs: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val progressAnime: SerializeList<ComposeHomeProgress> = persistentListOf(),
    val progressBook: SerializeList<ComposeHomeProgress> = persistentListOf(),
    val progressReal: SerializeList<ComposeHomeProgress> = persistentListOf(),

    /**
     * 用于固定条目显示位置的 ID 顺序列表 (Anime)
     */
    val animeOrder: SerializeList<Long> = persistentListOf(),
    /**
     * 用于固定条目显示位置的 ID 顺序列表 (Book)
     */
    val bookOrder: SerializeList<Long> = persistentListOf(),
    /**
     * 用于固定条目显示位置的 ID 顺序列表 (Real)
     */
    val realOrder: SerializeList<Long> = persistentListOf(),
)
