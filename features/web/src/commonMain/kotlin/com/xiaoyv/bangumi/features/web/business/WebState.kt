package com.xiaoyv.bangumi.features.web.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import io.ktor.http.Cookie
import kotlinx.collections.immutable.persistentListOf

/**
 * [WebState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class WebState(
    val title: String? = null,
    val url: String = "about:blank",
    val cookies: SerializeList<Cookie> = persistentListOf(),
)
