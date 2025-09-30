@file:OptIn(ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.popoverPresentationController

@Composable
actual fun rememberActionHandler(): ActionHandler {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    return remember(uriHandler, clipboard) { ActionHandler(uriHandler, clipboard) }
}

@Stable
actual class ActionHandler actual constructor(
    private val uriHandler: UriHandler,
    private val clipboard: Clipboard?,
) : CoroutineScope by MainScope() {

    actual fun shareContent(content: String) {
        // 准备分享内容
        val activity = UIActivityViewController(
            activityItems = listOf(content),
            applicationActivities = null
        )

        // 拿到顶层 VC 来 present
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        val rootVC = window?.rootViewController ?: return

        var presenter: UIViewController? = rootVC
        while (presenter?.presentedViewController != null) {
            presenter = presenter.presentedViewController
        }

        activity.popoverPresentationController?.sourceView = presenter?.view
        presenter?.presentViewController(activity, animated = true, completion = null)
    }

    actual fun openInBrowser(link: String) {
        runCatching { uriHandler.openUri(link) }
    }

    actual fun copyContent(content: String) {
        launch {
            runCatching {
                clipboard?.setClipEntry(ClipEntry.withPlainText(content))
            }
        }
    }
}