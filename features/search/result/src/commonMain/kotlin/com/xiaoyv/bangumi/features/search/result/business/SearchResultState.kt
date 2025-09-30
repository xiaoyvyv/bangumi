package com.xiaoyv.bangumi.features.search.result.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [SearchResultState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SearchResultState(
    val query: String = "",
    val tabs: SerializeList<ComposeTextTab<String>> = persistentListOf(),
    val filterSubjectSort: SerializeList<ComposeTextTab<String>> = persistentListOf(),

    val subjectParam: ListSubjectParam = ListSubjectParam.Empty,
    val characterParam: ListMonoParam = ListMonoParam.Empty,
    val personParam: ListMonoParam = ListMonoParam.Empty,
    val topicParam: ListTopicParam = ListTopicParam.Empty,
    val indexParam: ListIndexParam = ListIndexParam.Empty,
    val tagParam: ListTagParam = ListTagParam.Empty,
)
