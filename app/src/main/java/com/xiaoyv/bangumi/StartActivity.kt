package com.xiaoyv.bangumi

import android.app.Activity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.xiaoyv.bangumi.helper.RouteHelper

/**
 * Class: [StartActivity]
 *
 * @author why
 * @since 11/24/23
 */
class StartActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }
        RouteHelper.jumpHome()
        finish()
    }
}