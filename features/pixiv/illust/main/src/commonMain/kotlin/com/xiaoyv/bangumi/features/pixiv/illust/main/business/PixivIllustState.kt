package com.xiaoyv.bangumi.features.pixiv.illust.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPageInfo
import kotlinx.collections.immutable.persistentListOf

/**
 * [PixivIllustState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PixivIllustState(
    val illustId: Long = 0,
    val detail: ComposePixivIllustDetailBody = ComposePixivIllustDetailBody(),
    val pages: SerializeList<ComposePixivPageInfo> = persistentListOf(),
)
