package com.xiaoyv.bangumi.features.topic.detail.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [TopicDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TopicDetailViewModel(
    private val args: Screen.TopicDetail,
    private val subjectRepository: SubjectRepository,
    private val monoRepository: MonoRepository,
    private val blogRepository: BlogRepository,
    private val indexRepository: IndexRepository,
    private val topicRepository: TopicRepository,
    private val userManager: UserManager
) : BaseViewModel<TopicDetailState, TopicDetailSideEffect, TopicDetailEvent.Action>() {

    override fun initBaseState(): UiState<TopicDetailState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = TopicDetailState(
        type = args.type,
        id = args.id
    )

    override fun onEvent(event: TopicDetailEvent.Action) {
        when (event) {
            is TopicDetailEvent.Action.OnReactionClick -> onReactionClick(event.commentId, event.reaction)
            else -> Unit
        }
    }

    override suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.refreshSync() {
        when (args.type) {
            TopicType.TYPE_EP -> onLoadEpisodeTopicDetail()
            TopicType.TYPE_GROUP -> onLoadGroupTopicDetail()
            TopicType.TYPE_PERSON -> onLoadPersonTopicDetail()
            TopicType.TYPE_CRT -> onLoadCharacterTopicDetail()
            TopicType.TYPE_SUBJECT -> onLoadSubjectTopicDetail()
            TopicType.TYPE_INDEX -> onLoadIndexTopicDetail()
            TopicType.TYPE_BLOG -> onLoadBlogTopicDetail()
        }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadEpisodeTopicDetail() {
        awaitAll(
            block1 = { subjectRepository.fetchSubjectEpisode(args.id) },
            block2 = { subjectRepository.fetchSubjectEpisodeComments(args.id) }
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    episode = it.data1,
                    replies = it.data2.toImmutableList()
                )
            }
        }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadGroupTopicDetail() {
        topicRepository.fetchTopicDetail(args.id, TopicType.TYPE_GROUP)
            .onSuccess {
                reduceData {
                    state.copy(
                        topic = it,
                        replies = it.replies.subList(1, it.replies.size).toImmutableList()
                    )
                }
            }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadSubjectTopicDetail() {
        topicRepository.fetchTopicDetail(args.id, TopicType.TYPE_SUBJECT)
            .onSuccess {
                reduceData {
                    state.copy(
                        topic = it,
                        replies = it.replies.subList(1, it.replies.size).toImmutableList()
                    )
                }
            }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadPersonTopicDetail() {
        awaitAll(
            block1 = { monoRepository.fetchMonoDetail(args.id, MonoType.PERSON) },
            block2 = { monoRepository.fetchMonoComments(args.id, MonoType.PERSON) }
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    mono = ComposeMonoDisplay.from(MonoType.PERSON, it.data1),
                    replies = it.data2.toImmutableList()
                )
            }
        }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadCharacterTopicDetail() {
        awaitAll(
            block1 = { monoRepository.fetchMonoDetail(args.id, MonoType.CHARACTER) },
            block2 = { monoRepository.fetchMonoComments(args.id, MonoType.CHARACTER) }
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    mono = ComposeMonoDisplay.from(MonoType.CHARACTER, it.data1),
                    replies = it.data2.toImmutableList()
                )
            }
        }
    }


    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadIndexTopicDetail() {
        awaitAll(
            block1 = { indexRepository.fetchIndexDetail(args.id) },
            block2 = { indexRepository.fetchIndexComments(args.id) }
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    index = it.data1,
                    replies = it.data2.toImmutableList()
                )
            }
        }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadBlogTopicDetail() {
        awaitAll(
            block1 = { blogRepository.fetchBlogDetail(args.id) },
            block2 = { blogRepository.fetchBlogComments(args.id) },
            block3 = { blogRepository.fetchBlogRelateSubjects(args.id) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    blog = it.data1.copy(subjects = it.data3.toImmutableList()),
                    replies = it.data2.toImmutableList(),
                )
            }
        }
    }

    private fun onReactionClick(commentId: Long, reaction: ComposeReaction) = intent {
        val isLiked = reaction.users.any { it.id == userManager.userInfo.id }

        withActionLoading {
            when (args.type) {
                TopicType.TYPE_GROUP -> {
                    topicRepository.submitGroupReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_SUBJECT -> {
                    topicRepository.submitSubjectReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_BLOG -> {
                    blogRepository.submitBlogReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_EP -> {
                    subjectRepository.submitEpisodeReaction(commentId, if (isLiked) null else reaction.value)
                }

                else -> Result.failure(IllegalStateException("Not support!"))
            }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            val users = reaction.users
                .filter { it.id != userManager.userInfo.id }
                .toMutableList()

            val result = if (isLiked) {
                reaction.copy(users = users.toImmutableList())
            } else {
                reaction.copy(users = users.also { it.add(userManager.userInfo) }.toImmutableList())
            }

            reduceData {
                state.copy(
                    topic = state.topic.copy(
                        replies = state.topic.replies.refreshReaction(commentId, result)
                    ),
                    replies = state.replies.refreshReaction(commentId, result)
                )
            }
        }
    }

    private fun SerializeList<ComposeReply>.refreshReaction(
        commentId: Long,
        reaction: ComposeReaction
    ): ImmutableList<ComposeReply> {
        return this.map {
            if (it.id == commentId) {
                it.copy(reactions = it.reactions.map { rec ->
                    if (rec.value == reaction.value) reaction else rec
                }.toImmutableList())
            } else {
                it
            }
        }.toImmutableList()
    }
}
