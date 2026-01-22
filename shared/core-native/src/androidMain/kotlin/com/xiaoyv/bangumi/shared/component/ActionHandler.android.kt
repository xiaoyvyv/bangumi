package com.xiaoyv.bangumi.shared.component

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
actual fun rememberActionHandler(): ActionHandler {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    return remember(uriHandler, clipboard, context) {
        ActionHandler(context, uriHandler, clipboard)
    }
}


@Stable
actual class ActionHandler actual constructor(
    val uriHandler: UriHandler,
    val clipboard: Clipboard?,
) : CoroutineScope by MainScope() {
    private var context: Context? = null

    constructor(context: Context?, uriHandler: UriHandler, clipboard: Clipboard) : this(uriHandler, clipboard) {
        this.context = context
    }

    actual fun shareContent(content: String) {
        val ctx = context ?: return
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
            }
            val chooser = Intent.createChooser(intent, "分享")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(chooser)
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
                clipboard?.setClipEntry(ClipEntry(ClipData.newPlainText("Share", content)))
            }
        }
    }
}
