package com.xiaoyv.bangumi.features.index.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_success
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_blog
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_character
import com.xiaoyv.bangumi.core_resource.resources.global_episode
import com.xiaoyv.bangumi.core_resource.resources.global_game
import com.xiaoyv.bangumi.core_resource.resources.global_group_topic
import com.xiaoyv.bangumi.core_resource.resources.global_music
import com.xiaoyv.bangumi.core_resource.resources.global_person
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.core_resource.resources.global_subject_topic
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.getString

/**
 * [IndexDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class IndexDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.IndexDetail,
    private val indexRepository: IndexRepository,
    private val userManager: UserManager,
) : BaseViewModel<IndexDetailState, IndexDetailSideEffect, IndexDetailEvent.Action>(savedStateHandle) {

    override fun initBaseState(): BaseState<IndexDetailState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = IndexDetailState()

    override fun onEvent(event: IndexDetailEvent.Action) {
        when (event) {
            is IndexDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is IndexDetailEvent.Action.OnToggleBookmarkIndex -> onToggleBookmarkIndex()
        }
    }

    override suspend fun BaseSyntax<IndexDetailState, IndexDetailSideEffect>.refreshSync() {
        awaitAll(
            block1 = { indexRepository.fetchIndexDetail(args.id) },
            block2 = { if (userManager.isLogin) indexRepository.fetchIndexIsBookmarked(args.id) else Result.success(false) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            val tabs = createTabs(it.data1)

            reduceContent(forceRefresh = true) {
                state.copy(
                    index = it.data1.copy(isBookmarked = it.data2),
                    tabs = tabs
                )
            }
        }
    }

    private fun onToggleBookmarkIndex() = action {
        val isBookmarked = stateRaw.index.isBookmarked
        val toast = if (isBookmarked) getString(Res.string.collect_cancel_success) else getString(Res.string.collect_success)

        withActionLoading { indexRepository.submitBookmarkOrCancelIndex(args.id, !isBookmarked) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                reduceContent { state.copy(index = state.index.copy(isBookmarked = it)) }

                postToast { toast }
            }
    }

    private suspend fun createTabs(index: ComposeIndex): ImmutableList<ComposeTextTab<String>> {
        val stats = index.stats
        val items = mutableListOf<ComposeTextTab<String>>()

//        items.add(
//            ComposeTextTab(
//                IndexCatWebTabType.ALL,
//                labelText = getString(Res.string.global_all) + " " + index.total
//            )
//        )

        if (stats.subject.anime > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.SUBJECT_ANIME,
                    labelText = getString(Res.string.global_anime) + " " + stats.subject.anime
                )
            )
        }
        if (stats.subject.book > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.SUBJECT_BOOK,
                    labelText = getString(Res.string.global_book) + " " + stats.subject.book
                )
            )
        }
        if (stats.subject.music > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.SUBJECT_MUSIC,
                    labelText = getString(Res.string.global_music) + " " + stats.subject.music
                )
            )
        }
        if (stats.subject.game > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.SUBJECT_GAME,
                    labelText = getString(Res.string.global_game) + " " + stats.subject.game
                )
            )
        }
        if (stats.subject.real > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.SUBJECT_REAL,
                    labelText = getString(Res.string.global_real) + " " + stats.subject.real
                )
            )
        }

        if (stats.character > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.CHARACTER,
                    labelText = getString(Res.string.global_character) + " " + stats.character
                )
            )
        }
        if (stats.person > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.PERSON,
                    labelText = getString(Res.string.global_person) + " " + stats.person
                )
            )
        }
        if (stats.episode > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.EP,
                    labelText = getString(Res.string.global_episode) + " " + stats.episode
                )
            )
        }
        if (stats.blog > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.BLOG,
                    labelText = getString(Res.string.global_blog) + " " + stats.blog
                )
            )
        }
        if (stats.groupTopic > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.TOPIC_GROUP,
                    labelText = getString(Res.string.global_group_topic) + " " + stats.groupTopic
                )
            )
        }
        if (stats.subjectTopic > 0) {
            items.add(
                ComposeTextTab(
                    IndexCatWebTabType.TOPIC_SUBJECT,
                    labelText = getString(Res.string.global_subject_topic) + " " + stats.subjectTopic
                )
            )
        }

        return items.toImmutableList()
    }
}