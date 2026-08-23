package com.xiaoyv.bangumi.features.pixiv.illust.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.list.ListIllustType
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinIllustPageViewModel(param: ListIllustParam): IllustPageViewModel {
    return koinViewModel<IllustPageViewModel>(key = param.uniqueKey) {
        parametersOf(param)
    }
}

/**
 * [IllustPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class IllustPageViewModel(
    private val pixivRepository: PixivRepository,
    private val param: ListIllustParam,
) : BaseViewModel<IllustPageState, Any, IllustPageEvent.Action>() {

    private val illustPager = when (param.type) {
        ListIllustType.RANK -> pixivRepository.fetchIllustRankingPager(
            content = param.rank.content,
            mode = param.rank.mode,
            date = param.rank.date.takeIf { it.isNotBlank() }
        )

        ListIllustType.SEARCH -> pixivRepository.fetchIllustSearchPager(param.search)

        else -> pixivRepository.fetchIllustRankingPager(
            content = param.rank.content,
            mode = param.rank.mode,
            date = param.rank.date.takeIf { it.isNotBlank() }
        )
    }

    internal val illusts = illustPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = IllustPageState(
        param = param
    )

    override fun onEvent(event: IllustPageEvent.Action) {

    }
}
