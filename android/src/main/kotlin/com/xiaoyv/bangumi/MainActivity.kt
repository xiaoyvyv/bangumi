package com.xiaoyv.bangumi

import android.content.Intent
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.xiaoyv.bangumi.shared.component.ExternalUriHandler
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.printTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        requestHighestRefreshRate()
        setContent {
            App()
        }
        handleIncomingImage(intent)
        handlePixivUri(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingImage(intent)
        handlePixivUri(intent)
    }

    /**
     * pixiv://account/login?code=VhCxC-JW8jHhWWFCbb8oAKxEkwOlqngpm3VT500brqg&via=login
     */
    private fun handlePixivUri(intent: Intent) {
        val action = intent.action.orEmpty()
        if (action == Intent.ACTION_VIEW) {
            val uri = intent.data.toString()
            debugLog { "Uri:$uri" }
        }
    }

    private fun handleIncomingImage(intent: Intent) {
        val type = intent.type.orEmpty()

        when {
            intent.action == Intent.ACTION_SEND && type.startsWith("image/") -> {
                val imageUri = IntentCompat.getParcelableExtra(
                    intent,
                    Intent.EXTRA_STREAM,
                    Uri::class.java
                ) ?: return

                val detectType = intent.getIntExtra(Intent.EXTRA_REFERRER, 0)

                lifecycleScope.launch(Dispatchers.IO) {
                    val path = copyUriToInternalStorage(imageUri)
                    if (path != null) {
                        withContext(Dispatchers.Main) {
                            ExternalUriHandler.onImageReceived(path, detectType)
                        }
                    }
                }
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): String? {
        val context = this
        val fileName = "shared_image_${System.currentTimeMillis()}.jpg"
        val shareTmp = context.cacheDir.resolve("share").also {
            it.mkdirs()
        }
        val destinationFile = shareTmp.resolve(fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printTrace()
            null
        }
    }

    /**
     * 请求当前屏幕分辨率下可用的最高刷新率。
     *
     * 系统仍可能因省电、温控或设备刷新率策略降低实际刷新率。
     */
    private fun requestHighestRefreshRate() {
        val displayManager = getSystemService(DisplayManager::class.java)
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY) ?: return

        val currentMode = display.mode
        val preferredMode = display.supportedModes
            .asSequence()
            .filter {
                it.physicalWidth == currentMode.physicalWidth &&
                        it.physicalHeight == currentMode.physicalHeight
            }
            .maxByOrNull { it.refreshRate }

        if (preferredMode == null) {
            return
        }

        if (preferredMode.modeId == currentMode.modeId) return

        window.attributes = window.attributes.apply {
            preferredDisplayModeId = preferredMode.modeId
            preferredRefreshRate = preferredMode.refreshRate
        }
    }
}
