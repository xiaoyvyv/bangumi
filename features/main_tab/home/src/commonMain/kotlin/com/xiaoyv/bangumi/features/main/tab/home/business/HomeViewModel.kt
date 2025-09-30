package com.xiaoyv.bangumi.features.main.tab.home.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.currentWeekDay
import com.xiaoyv.bangumi.shared.core.utils.tomorrowWeekDay
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomepageCard
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString

/**
 * [HomeViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val subjectRepository: SubjectRepository,
    private val cacheRepository: CacheRepository,
    private val ugcRepository: UgcRepository,
    personalStateStore: PersonalStateStore,
) : BaseViewModel<HomeState, HomeSideEffect, HomeEvent.Action>(savedStateHandle) {

    private val cacheKey = stringPreferencesKey(name = "home_main")

    init {
        personalStateStore.state
            .drop(1)
            .onEach {
                saveCache()
            }
            .launchIn(viewModelScope)
    }

    private fun saveCache() {
        writeViewModelCache(
            cacheRepository = cacheRepository,
            cacheKey = cacheKey,
            saveCondition = { it.hotSubjects.isNotEmpty() }
        )
    }

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
    )

    override fun initSate(onCreate: Boolean) = HomeState()

    override suspend fun BaseSyntax<HomeState, HomeSideEffect>.refreshSync() {
        com.xiaoyv.bangumi.shared.core.utils.awaitAll(
            block1 = {
                subjectRepository.fetchSubjectList(
                    param = ListSubjectParam(
                        type = ListSubjectType.BROWSER,
                        browser = SubjectBrowserBody(
                            sort = SubjectSortBrowserType.TRENDS,
                            subjectType = SubjectType.ANIME,
                        )
                    ),
                    offset = 0, pageSize = 20
                )
            },
            block2 = { subjectRepository.fetchCalendar() },
            block3 = { subjectRepository.fetchTrends(SubjectType.ANIME, 20) },
            block4 = { subjectRepository.fetchTrends(SubjectType.BOOK, 20) },
            block5 = { subjectRepository.fetchTrends(SubjectType.MUSIC, 20) },
            block6 = { subjectRepository.fetchTrends(SubjectType.GAME, 20) },
            block7 = { subjectRepository.fetchTrends(SubjectType.REAL, 20) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            val weekDay = currentWeekDay().toString()
            val tomorrowWeekDay = tomorrowWeekDay().toString()
            val toadyItems = it.data2[weekDay].orEmpty()
            val tomorrowItems = it.data2[tomorrowWeekDay].orEmpty()
            val sections = persistentListOf(
                it.data3.toHomeSectionCard(SubjectType.ANIME),
                it.data4.toHomeSectionCard(SubjectType.BOOK),
                it.data5.toHomeSectionCard(SubjectType.MUSIC),
                it.data6.toHomeSectionCard(SubjectType.GAME),
                it.data7.toHomeSectionCard(SubjectType.REAL),
            )

            reduceContent {
                state.copy(
                    hotSubjects = it.data1.toPersistentList(),
                    todayCalendar = toadyItems.toPersistentList(),
                    todayTotal = tomorrowItems.sumOf { section -> section.watchers },
                    tomorrowCalendar = tomorrowItems.toPersistentList(),
                    tomorrow = tomorrowItems.sumOf { section -> section.watchers },
                    sections = sections
                )
            }
        }

        onRefreshIndexHomepage()

        saveCache()
    }

    override fun onEvent(event: HomeEvent.Action) {
        when (event) {
            is HomeEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is HomeEvent.Action.OnRefreshIndexHomepage -> onRefreshIndexHomepage()
        }
    }

    private fun onRefreshIndexHomepage() = action {
        ugcRepository.fetchIndexFocus().onSuccess {
            reduceContent { state.copy(indexFocus = it.toPersistentList()) }
        }
    }

    private suspend fun List<ComposeHomeSection>.toHomeSectionCard(@SubjectType type: Int): ComposeHomepageCard {
        return ComposeHomepageCard(
            type = type,
            title = "近期注目" + getString(SubjectType.string(type)),
            subjects = map { it.subject }.toPersistentList()
        )
    }
}