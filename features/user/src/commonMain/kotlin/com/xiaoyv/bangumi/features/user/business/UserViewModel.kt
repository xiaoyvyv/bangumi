package com.xiaoyv.bangumi.features.user.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.ResultZip2
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSectionTitle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollectionInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString

/**
 * [UserViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class UserViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.UserDetail,
    private val cacheRepository: CacheRepository,
    private val userRepository: UserRepository,
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<UserState, UserSideEffect, UserEvent.Action>(savedStateHandle) {
    private val cacheKey = stringPreferencesKey(name = "user_detail_" + args.username)

    init {
        personalStateStore.state
            .drop(1)
            .onEach {

                saveCache()
            }
            .launchIn(viewModelScope)
    }

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = {
            it.copy(timeMachine = it.timeMachine.distinctBy { it.key }.toPersistentList())
        }
    )

    override fun initSate(onCreate: Boolean) = UserState(
        username = args.username
    )

    private fun saveCache() = writeViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        saveCondition = { it.user != ComposeUser.Empty },
    )

    override fun onEvent(event: UserEvent.Action) {
        when (event) {
            is UserEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is UserEvent.Action.OnChangeSubjectTypeFilter -> onChangeSubjectTypeFilter(event.type)
            is UserEvent.Action.OnChangeCollectionTypeFilter -> onChangeCollectionTypeFilter(event.type)
            is UserEvent.Action.OnChangeCollectionSortFilter -> onChangeCollectionSortFilter(event.type)
        }
    }

    override suspend fun BaseSyntax<UserState, UserSideEffect>.refreshSync() {
        userRepository.fetchUserInfo(args.username)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(user = it) }

                refreshTimeMachine()
            }
    }

    private fun onChangeCollectionSortFilter(type: String) = action {
        reduceContent { state.copy(selectedCollectSort = type) }
        saveCache()
    }

    private fun onChangeCollectionTypeFilter(type: Int) = action {
        reduceContent { state.copy(selectedCollectType = type) }
        saveCache()
    }

    private fun onChangeSubjectTypeFilter(type: Int) = action {
        reduceContent { state.copy(selectedSubjectType = type) }
        saveCache()
    }

    private suspend fun refreshTimeMachine() = action {
        val user = stateRaw.user
        val batchSize = userManager.settings.ui.timeMachineGridLimit

        awaitAll(
            block1 = { fetchSingleSection(user, SubjectType.ANIME, batchSize) },
            block2 = { fetchSingleSection(user, SubjectType.BOOK, batchSize) },
            block3 = { fetchSingleSection(user, SubjectType.GAME, batchSize) },
            block4 = { fetchSingleSection(user, SubjectType.MUSIC, batchSize) },
            block5 = { fetchSingleSection(user, SubjectType.REAL, batchSize) }
        ).map {
            (it.data1 + it.data2 + it.data3 + it.data4 + it.data5).distinctBy { section -> section.key }
        }.onFailure {
            reduceError { it }
        }.onSuccess {
            reduceContent { state.copy(timeMachine = it.toImmutableList()) }
        }

        saveCache()
    }


    private suspend fun fetchSingleSection(
        user: ComposeUser,
        @SubjectType type: Int,
        limit: Int = 10,
    ): Result<ImmutableList<ComposeSection<ComposeSubject>>> {
        val result = awaitAll(
            block1 = { userRepository.fetchUserCollectionSubject(args.username, type, CollectionType.DONE, limit = limit) },
            block2 = { userRepository.fetchUserCollectionSubject(args.username, type, CollectionType.DOING, limit = limit) }
        ).getOrNull() ?: ResultZip2(emptyList(), emptyList())

        val sections = mutableListOf<ComposeSection<ComposeSubject>>()

        val collection = when (type) {
            SubjectType.BOOK -> user.stats.subject.book
            SubjectType.ANIME -> user.stats.subject.anime
            SubjectType.MUSIC -> user.stats.subject.music
            SubjectType.GAME -> user.stats.subject.game
            SubjectType.REAL -> user.stats.subject.real
            else -> ComposeCollectionInfo.Empty
        }
        debugLog { "collection:$collection" }

        val collectionInfo = buildList {
            if (collection.doing > 0) add(CollectionType.stringSync(type, CollectionType.DOING) + " " + collection.doing)
            if (collection.wish > 0) add(CollectionType.stringSync(type, CollectionType.WISH) + " " + collection.wish)
            if (collection.collect > 0) add(CollectionType.stringSync(type, CollectionType.DONE) + " " + collection.collect)
            if (collection.onHold > 0) add(CollectionType.stringSync(type, CollectionType.ASIDE) + " " + collection.onHold)
            if (collection.dropped > 0) add(CollectionType.stringSync(type, CollectionType.DROP) + " " + collection.dropped)
        }
        val collectionInfoText = if (collectionInfo.isEmpty()) "暂时没有收藏该类型" else collectionInfo.joinToString("、")

        // 有数据才添加块头
        if (result.data1.isNotEmpty() || result.data2.isNotEmpty()) {
            sections.add(
                ComposeSection(
                    key = type.toString(),
                    header = ComposeSectionTitle(
                        id = type.toString(),
                        title = "Ta的${getString(SubjectType.string(type))}",
                        subtitle = collectionInfoText
                    ),
                    item = ComposeSubject.Empty
                )
            )
        }

        result.data1.forEach {
            sections.add(
                ComposeSection(
                    key = it.id.toString(),
                    header = ComposeSectionTitle.Empty,
                    item = it
                )
            )
        }
        result.data2.forEach {
            sections.add(
                ComposeSection(
                    key = it.id.toString(),
                    header = ComposeSectionTitle.Empty,
                    item = it
                )
            )
        }
        return Result.success(sections.distinctBy { it.key }.toImmutableList())
    }
}