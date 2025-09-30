package com.xiaoyv.bangumi.features.article.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_hot
import com.xiaoyv.bangumi.core_resource.resources.global_master
import com.xiaoyv.bangumi.core_resource.resources.global_newest
import com.xiaoyv.bangumi.core_resource.resources.global_oldest
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.global_self
import com.xiaoyv.bangumi.features.article.ArticleArguments
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.CommentFilterType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.SortType
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * [ArticleViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class ArticleViewModel(
    savedStateHandle: SavedStateHandle,
    private val ugcRepository: UgcRepository,
    private val userManager: UserManager,
    private val cacheRepository: CacheRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<ArticleState, ArticleSideEffect, ArticleEvent.Action>(savedStateHandle) {
    private val args = ArticleArguments(savedStateHandle)

    private val rawComments: MutableList<ComposeComment> = mutableListOf()

    private val cacheKey = stringPreferencesKey(name = "article_detail_" + args.id + "_" + args.type)

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = { initSate(true).copy(title = it.title, article = it.article) }
    )

    override fun initSate(onCreate: Boolean) = ArticleState(
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

    override fun onEvent(event: ArticleEvent.Action) {
        when (event) {
            is ArticleEvent.Action.OnRefresh -> refresh(true)
            is ArticleEvent.Action.OnReactionClick -> onReactionClick(event.type, event.id, event.value)
            is ArticleEvent.Action.OnCommentTypeChange -> onCommentTypeChange(event.type)
            is ArticleEvent.Action.OnCommentSortChange -> onCommentSortChange(event.type)
            is ArticleEvent.Action.OnAppendComment -> onAppendComment(event.comment)
        }
    }

    override suspend fun BaseSyntax<ArticleState, ArticleSideEffect>.refreshSync() {
        ugcRepository.fetchTopicDetail(args.id, args.type).fold(
            onFailure = { reduceError { it } },
            onSuccess = {
                rawComments.clear()
                rawComments.addAll(it.comments)

                stateRaw.refreshComments(article = it)
                    .copy(lastViewed = System.currentTimeMillis())
                    .let { state -> reduceContent { state } }
            }
        )
    }

    private fun onCommentTypeChange(@CommentFilterType type: Int) = action {
        stateRaw.refreshComments(selectedCommentTypeFilter = type)
            .let { state -> reduceContent { state } }
    }

    private fun onCommentSortChange(@SortType type: Int) = action {
        stateRaw.refreshComments(selectedCommentSortFilter = type)
            .let { state -> reduceContent { state } }
    }

    private fun onReactionClick(@CommentType type: Int, id: String, value: String) = action {
        val authorId = if (args.type == RakuenIdType.TYPE_BLOG && stateRaw.article.user.id == 0L) {
            userRepository.fetchUserInfo(stateRaw.article.user.username).map { it.id }.getOrDefault(0)
        } else {
            stateRaw.article.user.id
        }

        ugcRepository.submitReaction(
            mainId = if (args.type == RakuenIdType.TYPE_BLOG) authorId else args.id,
            type = type,
            id = id,
            value = value
        ).onSuccessWithErrorToast { data ->
            reduceContent {
                val reactions = state.article.reactions
                val newMap = if (reactions.containsKey(id)) {
                    reactions
                        .mapValues { if (it.key == id) data.toPersistentList() else it.value }
                        .toPersistentMap()
                } else {
                    reactions.toMutableMap()
                        .apply { put(id, data.toPersistentList()) }
                        .toPersistentMap()
                }

                state.copy(article = state.article.copy(reactions = newMap))
            }
        }
    }

    /**
     * 评论发送成功，向 UI 添加评论
     */
    private fun onAppendComment(comment: ComposeNewReply) = action {
        val article = stateRaw.article
        val mains = comment.posts.main
        val subs = comment.posts.sub

        // 子评论
        subs.forEach { main ->
            val parentIdx = rawComments.indexOfFirst { it.id == main.key }
            if (parentIdx != -1) {
                val parent = rawComments[parentIdx]
                val comments = parent.children.toMutableList()

                main.value.forEach { item ->
                    val index = comments.indexOfFirst { it.id == item.pstId }
                    if (index == -1) {
                        comments.add(
                            item.toComposeComment(
                                parent = parent,
                                commentType = CommentType.fromRakuenIdType(article.type),
                                floor = parent.floor + "-${comments.size + 1}",
                            )
                        )
                    }
                }

                rawComments[parentIdx] = parent.copy(children = comments.toImmutableList())
            }
        }

        // 主评论
        mains.forEach { main ->
            val element = main.value.toComposeComment(
                parent = null,
                commentType = CommentType.fromRakuenIdType(article.type),
                floor = "#${rawComments.size + 1}"
            )
            val index = rawComments.indexOfFirst { it.id == main.key }
            if (index == -1) {
                debugLog { "添加主评论：${main.value} ${element.replyParam}" }

                rawComments.add(element)
            }
        }

        // 若是主评论有变动则修改一下排序，让其置顶显示自己刚刚发布的评论
        if (mains.isNotEmpty()) {
            stateRaw.refreshComments(
                selectedCommentTypeFilter = CommentFilterType.ALL,
                selectedCommentSortFilter = SortType.NEWEST,
                lastViewed = System.currentTimeMillis()
            )
        } else {
            stateRaw.refreshComments(lastViewed = System.currentTimeMillis())
        }.let { state ->
            reduceContent { state }
        }
    }

    /**
     * 刷新评论数据
     */
    private suspend fun ArticleState.refreshComments(
        article: ComposeTopicDetail = this.article,
        @CommentFilterType selectedCommentTypeFilter: Int = this.selectedCommentTypeFilter,
        @SortType selectedCommentSortFilter: Int = this.selectedCommentSortFilter,
        lastViewed: Long = this.lastViewed,
    ): ArticleState {
        val comments = rawComments.applyCommentFilters(
            selectedCommentTypeFilter = selectedCommentTypeFilter,
            selectedCommentSortFilter = selectedCommentSortFilter,
            masterUsername = article.user.username,
            reactions = article.reactions,
        )

        return copy(
            selectedCommentTypeFilter = selectedCommentTypeFilter,
            selectedCommentSortFilter = selectedCommentSortFilter,
            article = article.copy(
                comments = comments,
                commentCount = comments.sumOf { comment ->
                    if (comment.children.isEmpty()) 1 else 1 + comment.children.size
                }
            ),
            lastViewed = lastViewed
        )
    }

    /**
     * 刷新评论数据，排序和过滤项目实现
     */
    private suspend fun List<ComposeComment>.applyCommentFilters(
        masterUsername: String,
        reactions: SerializeMap<String, SerializeList<ComposeReaction>>,
        @CommentFilterType selectedCommentTypeFilter: Int,
        @SortType selectedCommentSortFilter: Int,
    ): SerializeList<ComposeComment> {
        val comments = toMutableList()
        val self = userManager.userInfo.username
        val friends = userManager.friends.map { it.username }

        return withContext(Dispatchers.IO) {
            comments.asSequence()
                .filter {
                    when (selectedCommentTypeFilter) {
                        CommentFilterType.ALL -> true
                        CommentFilterType.REACTION -> reactions.containsKey(it.id) || it.children.any { comment ->
                            reactions.containsKey(comment.id)
                        }

                        CommentFilterType.MASTER -> it.user.username == masterUsername || it.children.any { comment ->
                            comment.user.username == masterUsername
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
                    when (selectedCommentSortFilter) {
                        SortType.NEWEST -> o2.id.compareTo(o1.id)
                        SortType.OLDEST -> o1.id.compareTo(o2.id)
                        SortType.HOT -> {
                            val cmp = o2.children.size.compareTo(o1.children.size)
                            if (cmp != 0) cmp
                            else reactions[o2.id].orEmpty().size.compareTo(reactions[o1.id].orEmpty().size)
                        }

                        else -> 0
                    }
                }
                .toPersistentList()
        }
    }
}