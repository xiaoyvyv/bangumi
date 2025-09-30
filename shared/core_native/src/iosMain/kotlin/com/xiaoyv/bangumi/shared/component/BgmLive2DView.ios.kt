package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.live2d.LCubismSdk
import com.xiaoyv.bangumi.shared.live2d.Live2DView
import com.xiaoyv.bangumi.shared.snizzors.SnizzorsUIView
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero


@Stable
actual class BgmLive2DState actual constructor() {
    private var live2DView by mutableStateOf<Live2DView?>(null)

    actual fun loadModel(modelName: String, modelDir: String) {
        live2DView?.loadModel(modelName, modelDir)
    }

    fun factory() = Live2DView(frame = CGRectZero.readValue())

    fun update(view: Live2DView) {
        live2DView = view.apply {
            onModelTapEvent = { modelName, x, y ->
                val expressionIds = getAllExpressionIds().orEmpty().filterIsInstance<String>()
                val motionsIds = getAllMotionIds().orEmpty().filterIsInstance<String>()
                println("expressionIds:${expressionIds.joinToString(",")}")
                println("motionsIds:${motionsIds.joinToString(",")}")
                when {
                    isTapArea("Hair", x, y) -> {
                        println("onModelTapEvent: TapHair")
                    }

                    isTapArea("Face", x, y) -> {
                        println("onModelTapEvent: TapFace")
                    }

                    isTapArea("Body", x, y) -> {
                        println("onModelTapEvent: TapBody")
                    }

                    else -> {
                        println("onModelTapEvent: $modelName, $x, $y")
                    }
                }
            }
        }
    }

    fun onRelease(view: Live2DView) {
        live2DView = null
        view.releaseView()
    }

    companion object {
        init {
            LCubismSdk.getInstance()?.initializeCubism()
        }
    }
}


@Composable
actual fun BgmLive2DView(modifier: Modifier, state: BgmLive2DState) {
    SnizzorsUIView(
        modifier = modifier,
        factory = state::factory,
        update = state::update,
        onRelease = state::onRelease
    )
}
