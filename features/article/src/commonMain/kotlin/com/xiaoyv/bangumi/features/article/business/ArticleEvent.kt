package com.xiaoyv.bangumi.features.article.business

import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.SortType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [ArticleEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class ArticleEvent {
    sealed class UI : ArticleEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
        data object OnCollapseHeader : UI()
    }

    sealed class Action : ArticleEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnReactionClick(@field:CommentType val type: Int, val id: String, val value: String) : Action()
        data class OnCommentTypeChange(@field:CommentType val type: Int) : Action()
        data class OnCommentSortChange(@field:SortType val type: Int) : Action()
        data class OnAppendComment(val comment: ComposeNewReply) : Action()
    }
}