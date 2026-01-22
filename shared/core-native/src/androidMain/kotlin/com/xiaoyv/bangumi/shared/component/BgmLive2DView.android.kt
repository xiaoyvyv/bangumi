package com.xiaoyv.bangumi.shared.component

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.live2d.sdk.cubism.view.LAppPal.printLog
import com.live2d.sdk.cubism.view.LCubismSdk
import com.live2d.sdk.cubism.view.Live2DView
import com.xiaoyv.bangumi.shared.application


@Stable
actual class BgmLive2DState actual constructor() {
    private var live2DView by mutableStateOf<Live2DView?>(null)

    actual fun loadModel(modelName: String, modelDir: String) {
        live2DView?.loadModel(modelName, modelDir)
    }

    fun factory(context: Context) = Live2DView(context)

    fun update(view: Live2DView) {
        live2DView = view.apply {
            onModelTapEvent = { modelName, x, y ->
                val expressionIds = getAllExpressionIds()
                val motionsIds = getAllMotionIds()
                printLog("expressionIds:${expressionIds.joinToString(",")}")
                printLog("motionsIds:${motionsIds.joinToString(",")}")
                when {
                    isTapArea("Hair", x, y) -> {
                        printLog("onModelTapEvent: TapHair")
                        setMotion("TapHair", 0)
                    }

                    isTapArea("Face", x, y) -> {
                        printLog("onModelTapEvent: TapFace")
                        setMotion("TapFace", 0)
                    }

                    isTapArea("Body", x, y) -> {
                        printLog("onModelTapEvent: TapBody")
                        setMotion("TapBody", 0)
                    }

                    else -> {
                        printLog("onModelTapEvent: $modelName, $x, $y")
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
            LCubismSdk.instance.initializeCubism(application)
        }
    }
}


@Composable
actual fun BgmLive2DView(modifier: Modifier, state: BgmLive2DState) {
    AndroidView(
        modifier = modifier,
        factory = state::factory,
        update = state::update,
        onRelease = state::onRelease
    )
}
