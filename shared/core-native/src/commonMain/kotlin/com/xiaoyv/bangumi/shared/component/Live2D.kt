package com.xiaoyv.bangumi.shared.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange

@Composable
fun rememberLive2DState(workDir: String = ""): Live2DState {
    return remember(workDir) { Live2DState(workDir) }
}

@Stable
expect class Live2DState(workDir: String = "") {
    var workDir: String
    val isLoading: Boolean
    val isLoaded: Boolean
    val modelName: String
    val currentMotion: String
    val currentExpression: String
    val availableMotions: List<String>
    val availableExpressions: List<String>

    /**
     * 发送触摸事件 (action: 0=DOWN, 1=MOVE, 2=UP)
     */
    fun onTouch(x: Float, y: Float, action: Int)

    /**
     * 发送拖拽追踪点坐标
     */
    fun onDrag(x: Float, y: Float)

    /**
     * 重置拖拽/触摸追踪状态
     */
    fun resetDrag()

    /**
     * 从 ZIP 文件加载 Live2D 模型
     * @param zipFilePath 可读写的 ZIP 完整路径
     * @param modelName 模型名称（解压后存放在 workDir/modelName 下）
     */
    fun loadModel(zipFilePath: String, modelName: String)

    /**
     * 播放指定分组的动作动画
     */
    fun setMotion(group: String, index: Int = 0)

    /**
     * 设置表情
     */
    fun setExpression(expressionId: String)

    /**
     * 检测指定坐标点击的部位名称（如 "Head", "Body" 等，未命中返回 null）
     */
    fun hitTest(x: Float, y: Float): String?

    /**
     * 获取当前加载模型中所有可用的动作组名称
     */
    fun getMotions(): List<String>

    /**
     * 获取当前加载模型中所有可用的表情名称
     */
    fun getExpressions(): List<String>
}

/**
 * 跨平台的 Compose Multiplatform Live2D 组件
 */
@Composable
expect fun Live2D(
    modifier: Modifier = Modifier,
    state: Live2DState = rememberLive2DState(),
)

/**
 * 跨平台通用 Live2D 看板娘长按拖拽与点击手势检测
 *
 * @param live2DState Live2D 控制状态
 * @param onDragOffset 拖拽偏移回调 (dx, dy)
 * @param onClick 点击回调，传入命中的部位名称（如 "Head"/"Body" 或 null），返回 Boolean 表示是否已处理点击
 */
fun Modifier.live2DOverlayGesture(
    live2DState: Live2DState,
    onDragOffset: (dx: Float, dy: Float) -> Unit,
    onClick: (hitArea: String?) -> Boolean,
): Modifier = pointerInput(live2DState) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        val pointerId = down.id

        live2DState.onTouch(down.position.x, down.position.y, 0)

        var isLongPress = false
        var isDrag = false

        val longPressResult = try {
            withTimeout(viewConfiguration.longPressTimeoutMillis) {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                    if (!change.pressed) {
                        return@withTimeout false
                    }

                    val distance = (change.position - down.position).getDistance()
                    if (distance > viewConfiguration.touchSlop) {
                        return@withTimeout false
                    }
                }
                false
            }
        } catch (_: PointerEventTimeoutCancellationException) {
            true
        }

        if (longPressResult) {
            isLongPress = true
            down.consume()
            live2DState.resetDrag()
        }

        if (isLongPress) {
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                    if (!change.pressed) {
                        break
                    }

                    val dragAmount = change.positionChange()
                    change.consume()

                    onDragOffset(dragAmount.x, dragAmount.y)
                }
            } finally {
                live2DState.resetDrag()
            }
        } else {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                if (!change.pressed) {
                    live2DState.onTouch(change.position.x, change.position.y, 2)
                    live2DState.resetDrag()

                    if (!isDrag) {
                        val hitArea = live2DState.hitTest(change.position.x, change.position.y)
                        val hit = hitArea.takeIf { !it.isNullOrEmpty() }
                        onClick(hit)
                    }
                    break
                }

                val distance = (change.position - down.position).getDistance()
                if (distance > viewConfiguration.touchSlop) {
                    isDrag = true
                    live2DState.onTouch(change.position.x, change.position.y, 1)
                    live2DState.onDrag(change.position.x, change.position.y)
                }
            }
        }
    }
}
