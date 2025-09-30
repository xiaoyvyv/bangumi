package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
private fun rememberImeHeight(): Dp {
    val density = LocalDensity.current
    val imeHeight = WindowInsets.ime.getBottom(density)
    val navHeight = WindowInsets.navigationBars.getBottom(density)
    return with(LocalDensity.current) {
        (imeHeight - navHeight).coerceAtLeast(0).toDp()
    }
}

@Composable
private fun rememberImeMaxHeight(defaultHeight: Dp = 100.dp): Dp {
    val density = LocalDensity.current
    var maxHeight by rememberSaveable { mutableStateOf(0) }

    // 当前 ime | nav 高度（像素）
    val imeHeight = WindowInsets.ime.getBottom(density)
    val navHeight = WindowInsets.navigationBars.getBottom(density)

    // 更新最大高度
    LaunchedEffect(imeHeight, navHeight) {
        val rawImeHeight = (imeHeight - navHeight).coerceAtLeast(0)
        if (rawImeHeight > maxHeight) {
            maxHeight = rawImeHeight
        }
    }

    return if (maxHeight > 0) with(density) { maxHeight.toDp() } else defaultHeight
}

@Composable
fun rememberImePanelState(onResetPanel: () -> Unit = {}): ImePanelState {
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeDp = rememberImeHeight()
    val imeMaxDp = rememberImeMaxHeight()
    val state = remember(keyboardController) {
        ImePanelState().apply { this.keyboardController = keyboardController }
    }
    val currentOnResetPanel by rememberUpdatedState(onResetPanel)

    DisposableEffect(state) {
        onDispose {
            state.cancel()
        }
    }

    LaunchedEffect(imeDp, imeMaxDp, currentOnResetPanel) {
        state.imeDp = imeDp
        state.imeMaxDp = imeMaxDp

        if (imeDp == imeMaxDp) {
            state.reset()
            currentOnResetPanel()
        }
    }
    return state
}

@Stable
class ImePanelState : CoroutineScope by MainScope() {

    var showPanel by mutableStateOf(false)
        private set

    var imeDp by mutableStateOf(0.dp)
        internal set

    var imeMaxDp by mutableStateOf(0.dp)
        internal set

    var keyboardController: SoftwareKeyboardController? = null
        internal set

    private var job: Job? = null

    internal fun reset() {
        showPanel = false
    }

    fun togglePanel(visible: Boolean, onFinish: () -> Unit = {}) {
        job?.cancel()

        if (visible) {
            keyboardController?.hide()
            showPanel = true
            onFinish()
        } else {
            keyboardController?.show()
            job = launch {
                withTimeoutOrNull(1000) {
                    while (isActive && imeMaxDp != imeDp) {
                        delay(50)
                    }
                }
                showPanel = false
                onFinish()
            }
        }
    }
}

/**
 * 键盘表情面板，兼容虚拟键盘高度变化，支持自适应且面板在键盘切换期间，高度不会跳动
 */
@Composable
fun ImePanelColumn(
    state: ImePanelState,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    panelContent: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier, verticalArrangement, horizontalAlignment) {
        content()

        if (state.showPanel) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(state.imeMaxDp),
                content = panelContent
            )
        } else {
            Spacer(Modifier.height(state.imeDp))
        }
    }
}