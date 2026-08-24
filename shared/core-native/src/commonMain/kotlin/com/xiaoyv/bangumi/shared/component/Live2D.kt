package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun rememberLive2DState(): Live2DState {
    return remember { Live2DState() }
}

@Stable
expect class Live2DState() {
    /**
     * 加载指定目录和模型的 Live2D 配置文件
     * @param modelDir 包含 .model3.json 的文件夹路径
     * @param modelJsonName 模型主配置文件名（例如 bangumi_black_musume_2026_parts.model3.json）
     */
    fun loadModel(modelDir: String, modelJsonName: String)

    /**
     * 播放指定分组的动作动画
     * @param group 动作分组名称（例如 "Idle", "Tap", "Sleeping" 等）
     * @param index 组内动作索引，默认为 0
     */
    fun setMotion(group: String, index: Int = 0)

    /**
     * 设置表情
     * @param expressionId 表情 ID
     */
    fun setExpression(expressionId: String)

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
    state: Live2DState = rememberLive2DState()
)
