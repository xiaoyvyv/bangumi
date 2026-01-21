package com.xiaoyv.bangumi.shared.ui.component.navigation

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

@Serializable
enum class LaunchMode {
    DEFAULT,
    SINGLE_TASK,
    SINGLE_TOP,
}

@Serializable
sealed interface StackAction {
    @Serializable
    data object None : StackAction

    @Serializable
    data object ClearAll : StackAction

    @Serializable
    data class PopUpTo(
        val destination: NavKey,
        val inclusive: Boolean = false
    ) : StackAction
}

@Stable
class Navigator(startDestination: NavKey = Screen.Splash) {
    private val debounceMap = HashMap<NavKey, Long>()

    val backStack = NavBackStack(startDestination)

    fun navigate(
        route: NavKey,
        launchMode: LaunchMode? = null,
        stackAction: StackAction? = null
    ) {
        val launchMode = launchMode ?: if (route is Screen) route.mode else LaunchMode.DEFAULT
        val stackAction = stackAction ?: if (route is Screen) route.stackAction else StackAction.None

        when (route) {
            is Screen.Tracking,
            is Screen.Dollars,
            is Screen.SettingsPrivacy,
            is Screen.MessageMain,
            is Screen.MessageChat,
            is Screen.Notification,
            is Screen.SettingsAccount,
            is Screen.SettingsBlock -> requireLogin {
                navigateInternal(route, launchMode, stackAction)
            }

            else -> navigateInternal(route, launchMode, stackAction)
        }
    }

    private fun navigateInternal(
        route: NavKey,
        mode: LaunchMode,
        stackAction: StackAction
    ) {
        debounce(route) {
            applyStackAction(stackAction)
            applyLaunchMode(route, mode)
        }
    }

    private fun applyStackAction(action: StackAction) {
        when (action) {
            is StackAction.None -> Unit
            is StackAction.ClearAll -> backStack.clear()
            is StackAction.PopUpTo -> {
                val index = backStack.indexOfLast { it == action.destination }
                if (index >= 0) {
                    val from = if (action.inclusive) index else index + 1
                    backStack.subList(from, backStack.size).clear()
                }
            }
        }
    }

    private fun applyLaunchMode(route: NavKey, mode: LaunchMode) {
        when (mode) {
            LaunchMode.DEFAULT -> backStack.add(route)

            LaunchMode.SINGLE_TOP -> {
                if (backStack.lastOrNull() != route) {
                    backStack.add(route)
                }
            }

            LaunchMode.SINGLE_TASK -> {
                val index = backStack.indexOfLast { it == route }
                if (index >= 0) {
                    backStack.subList(index + 1, backStack.size).clear()
                } else {
                    backStack.add(route)
                }
            }
        }
    }

    fun goBack(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeLast()
        return true
    }

    private inline fun requireLogin(block: () -> Unit) {
        val manager = KoinPlatform.getKoin().get<UserManager>()
        if (manager.isLogin) block() else {
            navigateInternal(
                Screen.SignIn,
                LaunchMode.SINGLE_TOP,
                StackAction.None
            )
        }
    }

    private inline fun debounce(
        key: NavKey,
        duration: Int = AnimationConstants.DefaultDurationMillis,
        block: () -> Unit,
    ) {
        val currentTime = System.currentTimeMillis()
        val lastClickTime = debounceMap[key] ?: 0L
        val diff = currentTime - lastClickTime
        if (diff >= duration) {
            debounceMap[key] = currentTime
            block()
        } else {
            debugLog { "Navigator: skip diff = $diff" }
        }
    }
}
