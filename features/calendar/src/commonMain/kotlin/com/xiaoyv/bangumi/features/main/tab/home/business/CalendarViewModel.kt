package com.xiaoyv.bangumi.features.main.tab.home.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.features.main.tab.home.CalendarArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import kotlinx.collections.immutable.toPersistentMap

/**
 * [CalendarViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class CalendarViewModel(
    savedStateHandle: SavedStateHandle,
    private val cacheRepository: CacheRepository,
    private val subjectRepository: SubjectRepository,
) : BaseViewModel<CalendarState, CalendarSideEffect, CalendarEvent.Action>(savedStateHandle) {
    private val args = CalendarArguments(savedStateHandle)

    private val cacheKey = stringPreferencesKey(name = "calendar_${args.isToday}")


    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = { it.copy(isToday = args.isToday) }
    )

    override fun initSate(onCreate: Boolean) = CalendarState(
        isToday = args.isToday
    )

    private fun saveCache() {
        writeViewModelCache(
            cacheRepository = cacheRepository,
            cacheKey = cacheKey,
            saveCondition = { it.calendarMap.isNotEmpty() }
        )
    }

    override fun onEvent(event: CalendarEvent.Action) {
        when (event) {
            is CalendarEvent.Action.OnRefresh -> refresh(loading = event.loading)
            CalendarEvent.Action.OnChangeLayoutMode -> onChangeLayoutMode()
        }
    }

    override suspend fun BaseSyntax<CalendarState, CalendarSideEffect>.refreshSync() {
        subjectRepository.fetchCalendar()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(calendarMap = it.toPersistentMap()) }
            }

        saveCache()
    }

    private fun onChangeLayoutMode() = action {
        reduceContent { state.copy(isGrid = !state.isGrid) }

        saveCache()
    }
}