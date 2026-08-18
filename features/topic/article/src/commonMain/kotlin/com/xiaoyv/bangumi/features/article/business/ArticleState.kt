package com.xiaoyv.bangumi.features.article.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [ArticleState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class ArticleState(
    val title: String = "",
    val article: ComposeTopicDetail = ComposeTopicDetail.Empty,
    val selectedCommentSortFilter: Int = 0,
    val selectedCommentTypeFilter: Int = 0,
    val commentSortFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val commentTypeFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    val lastViewed: Long = 0,
)
