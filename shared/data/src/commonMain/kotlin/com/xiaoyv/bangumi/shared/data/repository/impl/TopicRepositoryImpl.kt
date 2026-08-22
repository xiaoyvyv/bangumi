package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.LikeCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryOffsetLimitPagingController

class TopicRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
) : TopicRepository {

    override fun fetchTopicPager(param: ListTopicParam): MemoryPagingController<ComposeTopic, Long> {
        return createMemoryOffsetLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.id },
            onLoadData = { offset ->
                when (param.type) {
                    ListTopicType.SUBJECT_ALL -> client.nextSubjectApi.getRecentSubjectTopics(
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.normalized(TopicType.TYPE_SUBJECT) }

                    ListTopicType.SUBJECT_TARGET -> client.nextSubjectApi.getSubjectTopics(
                        subjectID = param.subjectID,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.normalized(TopicType.TYPE_SUBJECT) }

                    ListTopicType.GROUP_TARGET -> client.nextGroupApi.getGroupTopics(
                        groupName = param.groupName,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.normalized(TopicType.TYPE_GROUP) }

                    ListTopicType.GROUP_ALL -> client.nextGroupApi.getRecentGroupTopics(
                        mode = param.mode,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.normalized(TopicType.TYPE_GROUP) }

                    ListTopicType.SEARCH -> client.appApi.fetchSearchTopic(
                        keyword = param.search.keyword,
                        exact = param.search.exact,
                        order = param.search.order,
                        page = offset.toApiPage(pagingConfig.pageSize),
                        size = pagingConfig.pageSize
                    ).data.records.map { topic -> topic.toComposeTopic() }

                    else -> error("unknown type")
                }
            }
        )
    }


    override suspend fun fetchTopicDetail(
        topicId: Long,
        @TopicType type: String
    ): Result<ComposeTopic> = client.requestNextTopicApi {
        when (type) {
            TopicType.TYPE_SUBJECT -> getSubjectTopic(topicId)
            TopicType.TYPE_GROUP -> getGroupTopic(topicId)
            else -> error("unknown type")
        }
    }.map { it.normalized(type) }

    override suspend fun deleteComment(
        type: String,
        commentId: Long,
    ): Result<Unit> = when (type) {
        TopicType.TYPE_GROUP -> client.requestNextTopicApi { deleteGroupPost(commentId) }
        TopicType.TYPE_SUBJECT -> client.requestNextTopicApi { deleteSubjectPost(commentId) }
        TopicType.TYPE_EP -> client.requestNextEpisodeApi { deleteEpisodeComment(commentId.toInt()) }
        TopicType.TYPE_PERSON -> client.requestNextPersonApi { deletePersonComment(commentId.toInt()) }
        TopicType.TYPE_CRT -> client.requestNextCharacterApi { deleteCharacterComment(commentId.toInt()) }
        TopicType.TYPE_INDEX -> client.requestNextIndexApi { deleteIndexComment(commentId) }
        TopicType.TYPE_BLOG -> client.requestNextBlogApi { deleteBlogComment(commentId) }
        else -> Result.failure(IllegalArgumentException("Unsupported topic type: $type"))
    }.map { }

    override suspend fun submitGroupReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> = client.requestNextTopicApi {
        if (value.isNullOrBlank()) {
            unlikeGroupPost(postID = commentId)
        } else {
            likeGroupPost(commentId, LikeCommentParam(value.toInt()))
        }
    }

    override suspend fun submitSubjectReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> = client.requestNextTopicApi {
        if (value.isNullOrBlank()) {
            unlikeSubjectPost(postID = commentId)
        } else {
            likeSubjectPost(commentId, LikeCommentParam(value.toInt()))
        }
    }

    override suspend fun submitSubjectCommentReaction(commentId: Long, value: String?): Result<Unit> = client.requestNextTopicApi {
        if (value.isNullOrBlank()) {
            unlikeSubjectComment(commentId)
        } else {
            likeSubjectComment(commentId, LikeCommentParam(value.toInt()))
        }
    }

    override suspend fun submitGroupComment(
        topicId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?
    ): Result<ComposeId> = client.requestNextTopicApi {
        createGroupReply(
            topicID = topicId, param = CreateCommentParam(
                content = content, turnstileToken = turnstile,
                replyTo = replyTo ?: 0
            )
        )
    }

    override suspend fun submitSubjectComment(
        topicId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?
    ): Result<ComposeId> = client.requestNextTopicApi {
        createSubjectReply(
            topicID = topicId, param = CreateCommentParam(
                content = content, turnstileToken = turnstile,
                replyTo = replyTo ?: 0
            )
        )
    }

    override suspend fun submitSubjectEpisodeComment(
        episodeId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?
    ): Result<ComposeId> = client.requestNextEpisodeApi {
        createEpisodeComment(
            episodeID = episodeId,
            param = CreateCommentParam(
                content = content,
                turnstileToken = turnstile,
                replyTo = replyTo ?: 0
            )
        )
    }
}
