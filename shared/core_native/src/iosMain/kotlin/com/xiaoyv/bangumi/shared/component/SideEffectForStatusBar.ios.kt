package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
actual fun SideEffectForStatusBar(darkTheme: Boolean) {
    UIApplication.sharedApplication.setStatusBarStyle(if (darkTheme) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent)
}
