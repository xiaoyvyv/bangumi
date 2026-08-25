package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun rememberLive2DState(
    workDir: String = "",
    onHitAreaClick: ((hitArea: String) -> Unit)? = null
): Live2DState {
    val state = remember(workDir) { Live2DState(workDir) }
    state.onHitAreaClick = onHitAreaClick
    return state
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
    var onHitAreaClick: ((hitArea: String) -> Unit)?

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
    onHitAreaClick: ((hitArea: String) -> Unit)? = null
)
