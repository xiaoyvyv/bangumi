package com.xiaoyv.bangumi.features.topic.page.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam

/**
 * [TopicPageState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TopicPageState(val param: ListTopicParam)
