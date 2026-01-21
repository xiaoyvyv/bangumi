package com.xiaoyv.bangumi.features.subject.browser.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SubjectBrowserViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SubjectBrowserViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.SubjectBrowser,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<SubjectBrowserState, SubjectBrowserSideEffect, SubjectBrowserEvent.Action>(savedStateHandle) {

    private val cacheKey = stringPreferencesKey(name = "subject_browser_${args.body.uniqueKey}")

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = false
    )

    private fun saveCache() {
        writeViewModelCache(
            cacheRepository = cacheRepository,
            cacheKey = cacheKey,
        )
    }

    override fun initSate(onCreate: Boolean) = SubjectBrowserState(
        title = args.title,
        param = ListSubjectParam(
            type = ListSubjectType.BROWSER,
            browser = if (args.body != SubjectBrowserBody.Empty) args.body else {
                SubjectBrowserBody(
                    subjectType = SubjectType.ANIME,
                    sort = SubjectSortBrowserType.COLLECTS,
                )
            }
        )
    )

    override fun onEvent(event: SubjectBrowserEvent.Action) {
        when (event) {
            is SubjectBrowserEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is SubjectBrowserEvent.Action.OnUpdateBrowserSubjectParam -> onUpdateBrowserSubjectParam(event.body)
            SubjectBrowserEvent.Action.OnChangeLayoutMode -> onChangeLayoutMode()
        }
    }

    private fun onChangeLayoutMode() = action {
        reduceContent {
            state.copy(
                param = state.param.copy(
                    ui = state.param.ui.copy(
                        gridLayout = !state.param.ui.gridLayout
                    )
                )
            )
        }

        saveCache()
    }

    private fun onUpdateBrowserSubjectParam(body: SubjectBrowserBody) = action {
        reduceContent { state.copy(param = state.param.copy(browser = body)) }
        saveCache()
    }
}