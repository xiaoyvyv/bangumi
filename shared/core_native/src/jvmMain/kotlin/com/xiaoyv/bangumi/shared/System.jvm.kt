package com.xiaoyv.bangumi.shared

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.xiaoyv.bangumi.shared.database.DatabaseDriverFactory
import com.xiaoyv.bangumi.shared.native.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import okio.Path.Companion.toPath
import java.awt.Desktop
import java.io.File
import java.lang.System
import java.net.URI
import java.net.URLEncoder
import kotlin.coroutines.CoroutineContext

actual object System {
    private var currentDir: File = File(System.getProperty("user.dir"))

    // TODO: Remove
    actual val isDebugType: Boolean = true
    actual val uiDispatcher: CoroutineContext = Dispatchers.Main

    actual val database: AppDatabase by lazy {
        AppDatabase(DatabaseDriverFactory.createDriver())
    }

    actual val datastore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                currentDir.resolve("bgm.preferences_pb").absolutePath.toPath()
            }
        )
    }

    actual fun createClipEntry(text: String): ClipEntry {
        return ClipEntry(nativeClipEntry = text)
    }

    actual fun userAgent(): String {
        return "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Ubuntu Chromium/70.0.3538.77 Chrome/70.0.3538.77 Safari/537.36"
    }

    actual fun currentTimeMillis() = kotlin.time.Clock.System.now().toEpochMilliseconds()

    actual fun launchDeeplinkSettings() {
    }

    actual fun log(tag: String, message: String) = println("[$tag]: $message")

    actual fun shareText(text: String) {
        val uri = URI("mailto:?body=${URLEncoder.encode(text, "UTF-8")}")
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri)
        }
    }

    @KtorDsl
    actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    dispatcher(
                        Dispatcher().apply {
                            maxRequests = 100
                            maxRequestsPerHost = 20
                        }
                    )
                }
            }
            block()
        }
    }

    actual suspend fun cleanCache(): Result<Boolean> {
        return Result.success(true)
    }
}