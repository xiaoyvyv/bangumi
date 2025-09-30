package com.xiaoyv.bangumi.features.message.chat

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getLong
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_ID
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_USERNAME
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.MessageChatRouteDefinition


data class MessageChatArguments(val id: Long, val username: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.getLong(EXTRA_ID),
        username = savedStateHandle.getString(EXTRA_USERNAME)
    )
}


fun NavHostController.navigateMessageChat(screen: Screen.MessageChat) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_ID, screen.id)
        param(EXTRA_USERNAME, screen.username)
    }
}

fun NavGraphBuilder.addMessageChatScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(MessageChatRouteDefinition) {
        MessageChatRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}