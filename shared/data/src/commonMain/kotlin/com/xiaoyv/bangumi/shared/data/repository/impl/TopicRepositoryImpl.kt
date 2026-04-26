package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.LikeEpisodeCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager

class TopicRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
) : TopicRepository {

    override fun fetchTopicPager(param: ListTopicParam): Pager<Int, ComposeTopic> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
            onLoadData = { offset ->
                when (param.type) {
                    ListTopicType.SUBJECT_ALL -> client.nextSubjectApi.getRecentSubjectTopics(
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.opt(TopicDetailType.TYPE_SUBJECT) }

                    ListTopicType.SUBJECT_TARGET -> client.nextSubjectApi.getSubjectTopics(
                        subjectID = param.subjectID,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.opt(TopicDetailType.TYPE_SUBJECT) }

                    ListTopicType.GROUP_TARGET -> client.nextGroupApi.getGroupTopics(
                        groupName = param.groupName,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.opt(TopicDetailType.TYPE_GROUP) }

                    ListTopicType.GROUP_ALL -> client.nextGroupApi.getRecentGroupTopics(
                        mode = param.mode,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { topic -> topic.opt(TopicDetailType.TYPE_GROUP) }

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
        @TopicDetailType type: String
    ): Result<ComposeTopic> = client.requestNextTopicApi {
        when (type) {
            TopicDetailType.TYPE_SUBJECT -> getSubjectTopic(topicId)
            TopicDetailType.TYPE_GROUP -> getGroupTopic(topicId)
            else -> error("unknown type")
        }
    }

    override suspend fun submitGroupReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> = client.requestNextTopicApi {
        if (value.isNullOrBlank()) {
            unlikeGroupPost(postID = commentId)
        } else {
            likeGroupPost(commentId, LikeEpisodeCommentRequest(value.toInt()))
        }
    }

    override suspend fun submitSubjectReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> = client.requestNextTopicApi {
        if (value.isNullOrBlank()) {
            unlikeSubjectPost(postID = commentId)
        } else {
            likeSubjectPost(commentId, LikeEpisodeCommentRequest(value.toInt()))
        }
    }
}