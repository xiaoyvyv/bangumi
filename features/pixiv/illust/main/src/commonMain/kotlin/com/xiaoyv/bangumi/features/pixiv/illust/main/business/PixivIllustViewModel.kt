package com.xiaoyv.bangumi.features.pixiv.illust.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [PixivIllustViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivIllustViewModel(
    private val illustId: Long,
    private val pixivRepository: PixivRepository,
) : BaseViewModel<PixivIllustState, PixivIllustSideEffect, PixivIllustEvent.Action>() {

    override fun initBaseState(): UiState<PixivIllustState> =
        initBaseLoadingState()

    override fun createInitialState() = PixivIllustState(illustId = illustId)

    override fun onEvent(event: PixivIllustEvent.Action) {
        when (event) {
            is PixivIllustEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
        }
    }

    override suspend fun Syntax<UiState<PixivIllustState>, UiSideEffect<PixivIllustSideEffect>>.refreshSync() {
        awaitAll(
            block1 = { pixivRepository.fetchIllustDetail(illustId) },
            block2 = { pixivRepository.fetchIllustPages(illustId) }
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData(forceRefresh = true) {
                state.copy(
                    detail = it.data1,
                    pages = it.data2.toPersistentList()
                )
            }
        }
    }
}
