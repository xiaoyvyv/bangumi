package com.xiaoyv.bangumi.features.preivew.main.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_save
import com.xiaoyv.bangumi.core_resource.resources.global_set_wallpaper
import com.xiaoyv.bangumi.core_resource.resources.global_share
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * [PreviewMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewMainViewModel(
    private val args: Screen.PreviewMain,
    private val choreRepository: ChoreRepository
) : BaseViewModel<PreviewMainState, PreviewMainSideEffect, PreviewMainEvent.Action>() {

    override fun createInitialState() = PreviewMainState(
        items = args.items.toPersistentList(),
        index = args.index,
        contextMenus = persistentListOf(
            ComposeTextTab(0, Res.string.global_save),
            ComposeTextTab(1, Res.string.global_share),
            ComposeTextTab(2, Res.string.global_set_wallpaper),
        ),
        title = if (args.items.size <= 1) "" else "${args.index + 1}/${args.items.size}"
    )

    override fun onEvent(event: PreviewMainEvent.Action) {
        when (event) {
            is PreviewMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PreviewMainEvent.Action.OnPageSelected -> onPageSelected(event.index)
            is PreviewMainEvent.Action.OnSaveMedia -> onSaveMedia()
            is PreviewMainEvent.Action.OnSetWallpaper -> onSetWallpaper()
            is PreviewMainEvent.Action.OnShareMedia -> onShareMedia()
        }
    }

    private fun onPageSelected(index: Int) = intent {
        reduceData {
            state.copy(
                index = index,
                title = if (state.items.size <= 1) "" else "${index + 1}/${state.items.size}"
            )
        }
    }

    private fun onSaveMedia() = intent {
        withActionLoading { choreRepository.fetchMediaPictureByUrl(state.items[state.index]) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { file ->
                postEffect { PreviewMainSideEffect.OnSaveMedia(file) }
            }
    }

    private fun onSetWallpaper() = intent {
        withActionLoading { choreRepository.fetchMediaPictureByUrl(state.items[state.index]) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { file ->
                postEffect { PreviewMainSideEffect.OnSetWallpaper(file) }
            }
    }

    private fun onShareMedia() = intent {
        withActionLoading { choreRepository.fetchMediaPictureByUrl(state.items[state.index]) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { file ->
                postEffect { PreviewMainSideEffect.OnShareMedia(file) }
            }
    }
}