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

    private fun applyLaunchMode(key: NavKey, mode: LaunchMode) {
        when (mode) {
            LaunchMode.DEFAULT -> backStack.add(key)
            LaunchMode.SINGLE_TOP -> backStack.singleTop(key)
            LaunchMode.SINGLE_TASK -> backStack.singleTask(key)
        }
    }

    fun goBack() = backStack.goBack()

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

val NavBackStack<NavKey>.current
    get() = lastOrNull() as? Screen ?: Screen.Empty

fun NavBackStack<NavKey>.goBack(): Boolean {
    if (size <= 1) return false
    removeLast()
    return true
}

fun NavBackStack<NavKey>.navigate(
    key: NavKey,
    popUpTo: NavKey? = null,
    popUpToInclusive: Boolean = false
) {
    if (popUpTo != null) {
        val index = indexOf(popUpTo)
        if (index != -1) {
            val cutIndex = if (popUpToInclusive) index else index + 1
            while (size > cutIndex) {
                removeLast()
            }
        }
    }
    if (lastOrNull() != key) {
        add(key)
    }
}

fun NavBackStack<NavKey>.moveTop(key: NavKey) {
    val index = indexOf(key)
    if (index == -1) {
        add(key)
        return
    }

    if (index == lastIndex) return

    add(key)
    removeAt(index)
}

fun NavBackStack<NavKey>.singleTask(key: NavKey) {
    val index = indexOfLast { it == key }
    if (index >= 0) {
        subList(index + 1, size).clear()
    } else {
        add(key)
    }
}

fun NavBackStack<NavKey>.singleTop(key: NavKey) {
    if (lastOrNull() != key) {
        add(key)
    }
}
