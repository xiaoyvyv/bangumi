package com.xiaoyv.bangumi.shared.data.manager.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composition
 */
val LocalSharedViewModel = staticCompositionLocalOf<SharedViewModel> { error("No UserManager provided") }
val LocalSharedModelStoreOwner = compositionLocalOf<ViewModelStoreOwner?> { null }
val LocalSharedState = compositionLocalOf { SharedState() }

/**
 * 是否隐藏标题栏返回按钮
 */
val LocalHideNavIcon = compositionLocalOf { false }

@Composable
fun shareViewModel() = koinViewModel<SharedViewModel>(viewModelStoreOwner = requireNotNull(LocalSharedModelStoreOwner.current))

@Composable
fun currentSettings() = LocalSharedState.current.settings

@Composable
fun currentMikanIdMap() = LocalSharedState.current.mikanIdMap

@Composable
fun currentMikanId(subjectId: Long): State<String> {
    val map = currentMikanIdMap()
    return produceState("", key1 = map) {
        var id = ""
        for (i in map) if (i.value == subjectId.toString()) {
            id = i.key
            break
        }
        value = id
    }
}