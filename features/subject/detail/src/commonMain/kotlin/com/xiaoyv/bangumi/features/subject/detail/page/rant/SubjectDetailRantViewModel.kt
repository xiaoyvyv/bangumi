package com.xiaoyv.bangumi.features.subject.detail.page.rant

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinSubjectDetailRantViewModel(subjectId: Long, @CollectionType type: Int): SubjectDetailRantViewModel {
    return koinViewModel(
        key = "$subjectId-$type",
        parameters = { parametersOf(subjectId, type) }
    )
}

class SubjectDetailRantViewModel(
    subjectId: Long,
    @CollectionType collectionType: Int,
    subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository,
    private val userManager: UserManager
) : BaseViewModel<Any, Any, SubjectDetailRantEvent>() {
    private val subjectCommentController = subjectRepository.fetchSubjectCommentPager(subjectId, collectionType)

    internal val subjectComments = subjectCommentController.flow.cachedIn(viewModelScope)

    override fun createInitialState() = Unit

    override fun onEvent(event: SubjectDetailRantEvent) {
        when (event) {
            is SubjectDetailRantEvent.OnReactionClick -> onReactionClick(event.comment, event.reaction)
        }
    }

    private fun onReactionClick(comment: ComposeReply, reaction: ComposeReaction) = intent {
        val isLiked = reaction.users.any { it.username == userManager.userInfo.username }
        val self = userManager.userInfo.username

        withActionLoading {
            topicRepository.submitSubjectCommentReaction(comment.id, if (isLiked) null else reaction.value)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            // 先从全部的贴贴移除自己
            val reactions = comment.reactions
                .map { it.copy(users = it.users.filter { user -> user.username != self }.toImmutableList()) }
                .toMutableList()

            // 评论没有该贴贴直接添加一个
            val newReactions = if (reactions.find { it.value == reaction.value } == null) {
                reactions.add(reaction.copy(users = persistentListOf(userManager.userInfo)))
                reactions
            } else {
                // 添加
                if (!isLiked) {
                    reactions.map {
                        if (it.value == reaction.value) {
                            val users = it.users.toMutableList()
                            users.add(userManager.userInfo)
                            it.copy(users = users.toImmutableList())
                        } else {
                            it
                        }
                    }
                } else {
                    reactions
                }
            }

            val updatedComment = comment.copy(
                reactions = newReactions.filter { it.users.isNotEmpty() }.toImmutableList()
            )

            subjectCommentController.replaceById(comment.id, updatedComment)
        }
    }
}
