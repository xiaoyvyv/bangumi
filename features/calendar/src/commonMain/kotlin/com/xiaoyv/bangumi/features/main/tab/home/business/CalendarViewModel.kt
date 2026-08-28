package com.xiaoyv.bangumi.features.main.tab.home.business

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentMap
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [CalendarViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class CalendarViewModel(
    private val args: Screen.Calendar,
    private val cacheRepository: CacheRepository,
    private val subjectRepository: SubjectRepository,
) : BaseViewModel<CalendarState, CalendarSideEffect, CalendarEvent.Action>() {

    private val cacheKey = byteArrayPreferencesKey(name = "calendar:${args.isToday}")


    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = { it.copy(isToday = args.isToday) }
    )

    override fun createInitialState() = CalendarState(
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
            is CalendarEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            CalendarEvent.Action.OnChangeLayoutMode -> onChangeLayoutMode()
        }
    }

    override suspend fun Syntax<UiState<CalendarState>, UiSideEffect<CalendarSideEffect>>.refreshSync() {
        subjectRepository.fetchCalendar()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData { state.copy(calendarMap = it.toPersistentMap()) }
            }

        saveCache()
    }

    private fun onChangeLayoutMode() = intent {
        reduceData { state.copy(isGrid = !state.isGrid) }

        saveCache()
    }
}
