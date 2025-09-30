package com.xiaoyv.bangumi.features.friend.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun koinFriendViewModel(param: ListUserParam): FriendViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [FriendViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class FriendViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val param: ListUserParam,
) : BaseViewModel<FriendState, FriendSideEffect, FriendEvent.Action>(savedStateHandle) {
    private val userPager = userRepository.fetchUserPager(param)
    val users = userPager.flow.cachedIn(viewModelScope)

    override fun initBaseState(): BaseState<FriendState> = if (param.ui.pageMode) {
        BaseState.Success(initSate(true))
    } else {
        BaseState.Loading()
    }

    override fun initSate(onCreate: Boolean) = FriendState(param = param)

    override fun onEvent(event: FriendEvent.Action) {
        when (event) {
            is FriendEvent.Action.OnRefresh -> refresh(event.loading)
        }
    }

    override suspend fun BaseSyntax<FriendState, FriendSideEffect>.refreshSync() {
        if (!param.ui.pageMode) userRepository.fetchUserList(param)
            .onFailure { reduceError { it } }
            .onSuccess {
                val (keys, items) = it.grouped()

                reduceContent(forceRefresh = true) {
                    state.copy(keys = keys, friends = items)
                }
            }
    }

    private fun List<ComposeUserDisplay>.grouped(): Pair<PersistentList<String>, PersistentList<FriendItem>> {
        val groups = groupBy { friend ->
            val first = friend.pinyin.firstOrNull()?.uppercaseChar()
            if (first != null && first in 'A'..'Z') {
                first.toString()
            } else {
                "*"
            }
        }

        val keys = groups.keys
            .sortedWith(compareBy { if (it == "*") "ZZZ" else it }) // 确保 * 排到最后

        val items = mutableStateListOf<FriendItem>()
        keys.fastForEach { char ->
            val friends = groups[char].orEmpty()
                .sortedBy { friend -> friend.pinyin }
                .map { friend -> FriendItem.Friend(friend) }
            items.add(FriendItem.Header(title = char))
            items.addAll(friends)
        }

        return keys.toPersistentList() to items.toPersistentList()
    }
}