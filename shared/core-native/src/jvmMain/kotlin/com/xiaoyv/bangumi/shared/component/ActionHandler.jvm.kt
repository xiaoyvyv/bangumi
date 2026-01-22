package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URLEncoder

@Composable
actual fun rememberActionHandler(): ActionHandler {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    return remember(uriHandler, clipboard) { ActionHandler(uriHandler, clipboard) }
}

@androidx.compose.runtime.Stable
actual class ActionHandler actual constructor(
    private val uriHandler: UriHandler,
    private val clipboard: Clipboard?,
) : CoroutineScope by MainScope() {
    actual fun shareContent(content: String) {
        // 优先尝试用邮件客户端
        if (Desktop.isDesktopSupported()) {
            try {
                val body = URLEncoder.encode(content, Charsets.UTF_8.name())
                val uri = URI("mailto:?body=$body")
                Desktop.getDesktop().mail(uri)
                return
            } catch (_: Exception) {
                // ignore and fallback
            }
        }

        // 回退：复制到剪贴板
        try {
            val sel = StringSelection(content)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(sel, null)
            println("内容已复制到剪贴板")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun openInBrowser(link: String) {
        runCatching { uriHandler.openUri(link) }
    }

    actual fun copyContent(content: String) {
        launch {
            runCatching {
                clipboard?.setClipEntry(ClipEntry(StringSelection(content)))
            }
        }
    }
}