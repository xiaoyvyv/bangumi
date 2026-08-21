package com.xiaoyv.bangumi.features.topic.detail.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_hot
import com.xiaoyv.bangumi.core_resource.resources.global_master
import com.xiaoyv.bangumi.core_resource.resources.global_newest
import com.xiaoyv.bangumi.core_resource.resources.global_oldest
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.global_self
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.CommentFilterType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SortType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.refreshReaction
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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
        id = args.id,
        commentTypeFilters = persistentListOf(
            ComposeTextTab(CommentFilterType.ALL, label = Res.string.global_all),
            ComposeTextTab(CommentFilterType.REACTION, label = Res.string.global_reaction),
            ComposeTextTab(CommentFilterType.MASTER, label = Res.string.global_master),
            ComposeTextTab(CommentFilterType.FRIEND, label = Res.string.global_friend),
            ComposeTextTab(CommentFilterType.SELF, label = Res.string.global_self),
        ),
        commentSortFilters = persistentListOf(
            ComposeTextTab(SortType.NEWEST, label = Res.string.global_newest),
            ComposeTextTab(SortType.OLDEST, label = Res.string.global_oldest),
            ComposeTextTab(SortType.HOT, label = Res.string.global_hot),
        )
    )

    override fun onEvent(event: TopicDetailEvent.Action) {
        when (event) {
            is TopicDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is TopicDetailEvent.Action.OnReactionClick -> onReactionClick(event.commentId, event.reaction)

            is TopicDetailEvent.Action.OnCommentTypeChange -> onCommentTypeChange(event.type)
            is TopicDetailEvent.Action.OnCommentSortChange -> onCommentSortChange(event.type)
            is TopicDetailEvent.Action.OnAppendComment -> onAppendComment(event.replyId)
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
            val displayReplies = applyCommentFilters(state.data, it.data2)

            reduceData {
                state.copy(
                    episode = it.data1,
                    replies = it.data2.toImmutableList(),
                    displayReplies = displayReplies
                )
            }
        }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadGroupTopicDetail() {
        topicRepository.fetchTopicDetail(args.id, TopicType.TYPE_GROUP)
            .onFailure { reduceError { it } }
            .onSuccess {
                val replies = it.replies.subList(1, it.replies.size)
                val displayReplies = applyCommentFilters(
                    state = state.data,
                    replies = replies
                )

                reduceData {
                    state.copy(
                        topic = it,
                        replies = replies,
                        displayReplies = displayReplies
                    )
                }
            }
    }

    suspend fun Syntax<UiState<TopicDetailState>, UiSideEffect<TopicDetailSideEffect>>.onLoadSubjectTopicDetail() {
        topicRepository.fetchTopicDetail(args.id, TopicType.TYPE_SUBJECT)
            .onFailure { reduceError { it } }
            .onSuccess {
                val replies = it.replies.subList(1, it.replies.size)
                val displayReplies = applyCommentFilters(
                    state = state.data,
                    replies = replies
                )

                reduceData {
                    state.copy(
                        topic = it,
                        replies = replies,
                        displayReplies = displayReplies
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
            val displayReplies = applyCommentFilters(state.data, it.data2)

            reduceData {
                state.copy(
                    mono = ComposeMonoDisplay.from(MonoType.PERSON, it.data1),
                    replies = it.data2.toImmutableList(),
                    displayReplies = displayReplies
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
            val displayReplies = applyCommentFilters(state.data, it.data2)

            reduceData {
                state.copy(
                    mono = ComposeMonoDisplay.from(MonoType.CHARACTER, it.data1),
                    replies = it.data2.toImmutableList(),
                    displayReplies = displayReplies
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
            val displayReplies = applyCommentFilters(state.data, it.data2)

            reduceData {
                state.copy(
                    index = it.data1,
                    replies = it.data2.toImmutableList(),
                    displayReplies = displayReplies
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
            val displayReplies = applyCommentFilters(state.data, it.data2)

            reduceData {
                state.copy(
                    blog = it.data1.copy(subjects = it.data3.toImmutableList()),
                    replies = it.data2.toImmutableList(),
                    displayReplies = displayReplies
                )
            }
        }
    }

    private fun onCommentTypeChange(@CommentFilterType type: Int) = intent {
        val displayReplies = applyCommentFilters(
            state = state.data.copy(selectedCommentTypeFilter = type),
            replies = state.data.replies
        )

        reduceData {
            state.copy(
                selectedCommentTypeFilter = type,
                displayReplies = displayReplies
            )
        }
    }

    private fun onCommentSortChange(@SortType type: Int) = intent {
        val displayReplies = applyCommentFilters(
            state = state.data.copy(selectedCommentSortFilter = type),
            replies = state.data.replies
        )

        reduceData {
            state.copy(
                selectedCommentSortFilter = type,
                displayReplies = displayReplies,
            )
        }
    }

    /**
     * 评论发送成功，向 UI 添加评论
     *
     * @param replyId 评论ID
     */
    private fun onAppendComment(replyId: Long) = intent {
        when (args.type) {
            TopicType.TYPE_GROUP -> onLoadGroupTopicDetail()
            TopicType.TYPE_SUBJECT -> onLoadSubjectTopicDetail()
            TopicType.TYPE_PERSON -> {
                monoRepository.fetchMonoComments(args.id, MonoType.PERSON).onSuccess {
                    val displayReplies = applyCommentFilters(state.data, it)

                    reduceData {
                        state.copy(
                            replies = it.toImmutableList(),
                            displayReplies = displayReplies
                        )
                    }
                }
            }

            TopicType.TYPE_EP -> {
                subjectRepository.fetchSubjectEpisodeComments(args.id).onSuccess {
                    val displayReplies = applyCommentFilters(state.data, it)

                    reduceData {
                        state.copy(
                            replies = it.toImmutableList(),
                            displayReplies = displayReplies
                        )
                    }
                }
            }

            TopicType.TYPE_CRT -> {
                monoRepository.fetchMonoComments(args.id, MonoType.CHARACTER).onSuccess {
                    val displayReplies = applyCommentFilters(state.data, it)

                    reduceData {
                        state.copy(
                            replies = it.toImmutableList(),
                            displayReplies = displayReplies
                        )
                    }
                }
            }

            TopicType.TYPE_INDEX -> {
                indexRepository.fetchIndexComments(args.id).onSuccess {
                    val displayReplies = applyCommentFilters(state.data, it)

                    reduceData {
                        state.copy(
                            replies = it.toImmutableList(),
                            displayReplies = displayReplies
                        )
                    }
                }
            }

            TopicType.TYPE_BLOG -> {
                blogRepository.fetchBlogComments(args.id).onSuccess {
                    val displayReplies = applyCommentFilters(state.data, it)

                    reduceData {
                        state.copy(
                            replies = it.toImmutableList(),
                            displayReplies = displayReplies
                        )
                    }
                }
            }
        }
    }

    private fun onReactionClick(commentId: Long, reaction: ComposeReaction) = intent {
        val isLiked = reaction.users.any { it.username == userManager.userInfo.username }

        withActionLoading {
            when (args.type) {
                TopicType.TYPE_GROUP -> {
                    topicRepository.submitGroupReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_SUBJECT -> {
                    topicRepository.submitSubjectReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_EP -> {
                    subjectRepository.submitEpisodeCommentReaction(commentId, if (isLiked) null else reaction.value)
                }

                TopicType.TYPE_BLOG -> {
                    blogRepository.submitBlogReaction(commentId, if (isLiked) null else reaction.value)
                }

                else -> Result.failure(IllegalStateException("Not support!"))
            }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            val replies = state.data.replies.refreshReaction(userManager, commentId, reaction)
            val displayReplies = applyCommentFilters(
                state = state.data,
                replies = replies
            )

            reduceData {
                state.copy(
                    topic = state.topic.copy(replies = state.topic.replies.refreshReaction(userManager, commentId, reaction)),
                    replies = replies,
                    displayReplies = displayReplies
                )
            }
        }
    }


    /**
     * 刷新评论数据，排序和过滤项目实现
     */
    private suspend fun applyCommentFilters(
        state: TopicDetailState,
        replies: List<ComposeReply>
    ): SerializeList<ComposeReply> {
        val self = userManager.userInfo.username
        val master = state.topic.creator.username
        val friends = userManager.friends.map { it.username }

        return withContext(Dispatchers.IO) {
            replies
                .asSequence()
                .filter {
                    when (state.selectedCommentTypeFilter) {
                        CommentFilterType.ALL -> true
                        CommentFilterType.REACTION -> it.reactions.isNotEmpty() || it.children.any { comment ->
                            comment.reactions.isNotEmpty()
                        }

                        CommentFilterType.MASTER -> it.user.username == master || it.children.any { comment ->
                            comment.user.username == master
                        }

                        CommentFilterType.SELF -> it.user.username == self || it.children.any { comment ->
                            comment.user.username == self
                        }

                        CommentFilterType.FRIEND -> friends.contains(it.user.username) || it.children.any { comment ->
                            friends.contains(comment.user.username)
                        }

                        else -> false
                    }
                }
                .sortedWith { o1, o2 ->
                    when (state.selectedCommentSortFilter) {
                        SortType.NEWEST -> o2.id.compareTo(o1.id)
                        SortType.OLDEST -> o1.id.compareTo(o2.id)
                        SortType.HOT -> {
                            val cmp = o2.children.size.compareTo(o1.children.size)
                            if (cmp != 0) cmp
                            else o2.reactions.size.compareTo(o1.reactions.size)
                        }

                        else -> 0
                    }
                }
                .toPersistentList()
        }
    }
}
