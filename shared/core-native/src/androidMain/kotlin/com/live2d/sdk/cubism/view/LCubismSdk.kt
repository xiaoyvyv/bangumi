package com.live2d.sdk.cubism.view

import android.content.Context
import com.live2d.sdk.cubism.framework.CubismFramework
import com.live2d.sdk.cubism.view.LAppPal.PrintLogFunction

class LCubismSdk private constructor() {
    private val cubismOption = CubismFramework.Option()

    lateinit var applicationContext: Context
        private set

    fun setApplicationContext(context: Context) {
        applicationContext = context
    }

    fun initializeCubism(context: Context) {
        setApplicationContext(context)
        cubismOption.logFunction = PrintLogFunction()
        cubismOption.loggingLevel = LAppDefine.cubismLoggingLevel

        CubismFramework.cleanUp()
        CubismFramework.startUp(cubismOption)

        LAppPal.updateTime()
    }

    fun releaseCubism() {
        CubismFramework.cleanUp()
    }

    companion object {
        @JvmStatic
        val instance by lazy { LCubismSdk() }
    }
}
