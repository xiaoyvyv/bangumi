package com.xiaoyv.bangumi.features.pixiv.tag.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTagInfoBody

/**
 * State rendered by the Pixiv tag detail page.
 */
@Immutable
data class PixivTagState(
    val tag: String = "",
    val tagInfo: ComposePixivTagInfoBody = ComposePixivTagInfoBody.Empty,
)
