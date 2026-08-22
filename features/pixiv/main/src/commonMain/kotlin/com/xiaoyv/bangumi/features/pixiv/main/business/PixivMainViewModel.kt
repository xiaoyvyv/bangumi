package com.xiaoyv.bangumi.features.pixiv.main.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_all
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_illust
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_manga
import com.xiaoyv.bangumi.core_resource.resources.pixiv_tab_ugoira
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [PixivMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivMainViewModel :
    BaseViewModel<PixivMainState, PixivMainSideEffect, PixivMainEvent.Action>() {

    override fun createInitialState() = PixivMainState(
        tabs = persistentListOf(
            ComposeTextTab(PixivRankingContentType.ALL.value, Res.string.pixiv_tab_all),
            ComposeTextTab(PixivRankingContentType.ILLUST.value, Res.string.pixiv_tab_illust),
            ComposeTextTab(PixivRankingContentType.UGOIRA.value, Res.string.pixiv_tab_ugoira),
            ComposeTextTab(PixivRankingContentType.MANGA.value, Res.string.pixiv_tab_manga),
        )
    )

    override fun onEvent(event: PixivMainEvent.Action) {
        when (event) {
            is PixivMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}