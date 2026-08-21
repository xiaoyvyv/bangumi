package com.xiaoyv.bangumi.features.topic.detail.business

import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.SortType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TopicDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TopicDetailEvent {
    sealed class UI : TopicDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TopicDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()

        data class OnReactionClick(val commentId: Long, val reaction: ComposeReaction) : Action()

        data class OnCommentTypeChange(@field:CommentType val type: Int) : Action()
        data class OnCommentSortChange(@field:SortType val type: Int) : Action()
        data class OnAppendComment(val replyId: Long) : Action()
    }
}