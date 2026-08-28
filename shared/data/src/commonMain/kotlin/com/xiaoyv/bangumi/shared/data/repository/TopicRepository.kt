package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface TopicRepository {
    fun fetchTopicPager(param: ListTopicParam): MemoryPagingController<ComposeTopicDetail, Long>


    suspend fun fetchTopicDetail(topicId: Long, @TopicType type: String): Result<ComposeTopicDetail>

    suspend fun deleteComment(@TopicType type: String, commentId: Long): Result<Unit>

    suspend fun submitGroupReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitSubjectReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitSubjectCommentReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitGroupComment(topicId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>

    suspend fun submitSubjectComment(topicId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>

    suspend fun submitSubjectEpisodeComment(episodeId: Long, content: String, turnstile: String, replyTo: Long? = null): Result<ComposeId>

    suspend fun submitCreateSubjectTopic(
        subjectId: Int,
        title: String,
        content: String,
        turnstile: String,
    ): Result<ComposeId>
}
