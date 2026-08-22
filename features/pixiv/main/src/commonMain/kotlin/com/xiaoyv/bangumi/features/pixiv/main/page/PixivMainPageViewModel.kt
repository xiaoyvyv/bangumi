package com.xiaoyv.bangumi.features.pixiv.main.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinPixivMainPageViewModel(
    content: String,
): PixivMainPageViewModel {
    return koinViewModel<PixivMainPageViewModel>(
        key = content,
        parameters = { parametersOf(content) }
    )
}

class PixivMainPageViewModel(
    pixivRepository: PixivRepository,
    private val content: String,
) : BaseViewModel<PixivMainPageState, PixivMainSideEffect, PixivMainEvent>() {
    private val rankingPager = pixivRepository.fetchIllustRankingPager(content = content, mode = "daily")

    internal val rankingFlow = rankingPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = PixivMainPageState(
        content = content,
    )

    override fun onEvent(event: PixivMainEvent) {

    }
}
