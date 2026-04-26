package com.xiaoyv.bangumi.features.friend.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import kotlinx.collections.immutable.persistentListOf

/**
 * [FriendState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class FriendState(
    val keys: SerializeList<String> = persistentListOf(),
    val friends: SerializeList<FriendItem> = persistentListOf(),
    val param: ListUserParam = ListUserParam.Empty,
)

@Immutable
sealed class FriendItem(val key: String) {
    @Immutable
    data class Friend(val friend: ComposeUserDisplay) : FriendItem(key = friend.user.username)

    @Immutable
    data class Header(val title: String) : FriendItem(key = title)
}


