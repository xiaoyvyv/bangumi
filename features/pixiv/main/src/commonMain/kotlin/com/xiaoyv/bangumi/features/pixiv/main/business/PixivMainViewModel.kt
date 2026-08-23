package com.xiaoyv.bangumi.features.pixiv.main.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_all
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_illust
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_manga
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_ugoira
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.usecase.PixivRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [PixivMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivMainViewModel(
    private val pixivRepoUseCase: PixivRepoUseCase,
    private val preferenceStore: PreferenceStore,
) : BaseViewModel<PixivMainState, PixivMainSideEffect, PixivMainEvent.Action>() {

    private val isPixivLogin: Boolean
        get() = preferenceStore.pixivTokenData.refreshToken.isNotBlank() || preferenceStore.pixivTokenData.accessToken.isNotBlank()

    private val userAvatar: String
        get() = preferenceStore.pixivTokenData.currentUser.profileImageUrls.maxUrl

    private val userId: Long
        get() = preferenceStore.pixivTokenData.currentUser.id.toLongOrNull() ?: 0

    override fun createInitialState() = PixivMainState(
        tabs = persistentListOf(
            ComposeTextTab(PixivRankingContentType.ALL, Res.string.pixiv_tab_all),
            ComposeTextTab(PixivRankingContentType.ILLUST, Res.string.pixiv_tab_illust),
            ComposeTextTab(PixivRankingContentType.UGOIRA, Res.string.pixiv_tab_ugoira),
            ComposeTextTab(PixivRankingContentType.MANGA, Res.string.pixiv_tab_manga),
        ),
        isPixivLogin = isPixivLogin,
        userId = userId,
        userAvatar = userAvatar,
    )

    override suspend fun Syntax<UiState<PixivMainState>, UiSideEffect<PixivMainSideEffect>>.refreshSync() {
        val isLogin = pixivRepoUseCase.checkAndSyncPixivLoginStatus()
        reduceData {
            state.copy(
                isPixivLogin = isLogin,
                userId = if (isLogin) userId else 0,
                userAvatar = if (isLogin) preferenceStore.pixivTokenData.currentUser.profileImageUrls.maxUrl else ""
            )
        }
    }

    override fun onEvent(event: PixivMainEvent.Action) {
        when (event) {
            is PixivMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}
