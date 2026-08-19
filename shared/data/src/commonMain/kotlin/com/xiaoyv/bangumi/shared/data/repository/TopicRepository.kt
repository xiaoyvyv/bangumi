package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic

interface TopicRepository {
    fun fetchTopicPager(param: ListTopicParam): Pager<Int, ComposeTopic>


    suspend fun fetchTopicDetail(topicId: Long, @TopicType type: String): Result<ComposeTopic>

    suspend fun submitGroupReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitSubjectReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitGroupComment(topicId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>

    suspend fun submitSubjectComment(topicId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>

    suspend fun submitSubjectEpisodeComment(episodeId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>
}