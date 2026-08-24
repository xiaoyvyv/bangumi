package com.xiaoyv.bangumi.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
actual class Live2DState actual constructor() {
    actual fun loadModel(modelDir: String, modelJsonName: String) {}
    actual fun setMotion(group: String, index: Int) {}
    actual fun setExpression(expressionId: String) {}
    actual fun getMotions(): List<String> = emptyList()
    actual fun getExpressions(): List<String> = emptyList()
}

@Composable
actual fun Live2D(
    modifier: Modifier,
    state: Live2DState
) {
    Box(modifier = modifier)
}
