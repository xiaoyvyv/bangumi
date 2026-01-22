package com.xiaoyv.bangumi.shared

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.fetch.SourceFetchResult
import com.xiaoyv.bangumi.shared.avif.AvifFrame
import com.xiaoyv.bangumi.shared.avif.IosAvifDecoder
import com.xiaoyv.bangumi.shared.avif.PlatformBitmap
import com.xiaoyv.bangumi.shared.database.DatabaseDriverFactory
import com.xiaoyv.bangumi.shared.native.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import okio.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalNativeApi

fun AvifFrame.getBitmapResult(): Result<PlatformBitmap> {
    return runCatching {
        createPlatformBitmap().also {
            decodeFrame(it)
        }
    }
}

fun AvifFrame.createPlatformBitmap(): PlatformBitmap {
    return createPlatformBitmap(getWidth(), getHeight())
}

fun createPlatformBitmap(width: Int, height: Int): PlatformBitmap {
    return Bitmap().apply {
        allocPixels(
            ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL),
        )
    }
}

fun PlatformBitmap.asImageBitmap(): ImageBitmap {
    return this.asComposeImageBitmap()
}

actual object System {
    @OptIn(ExperimentalNativeApi::class)
    actual val isDebugType: Boolean = Platform.isDebugBinary

    actual val database: AppDatabase by lazy {
        AppDatabase(DatabaseDriverFactory.createDriver())
    }

    actual val datastore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { (fileDirectory() + "/bgm.preferences_pb").toPath() }
        )
    }

    actual fun currentTimeMillis() = kotlin.time.Clock.System.now().toEpochMilliseconds()


    actual fun log(tag: String, message: String) = println("[$tag]: $message")

    actual fun launchDeeplinkSettings() {
    }

    actual fun userAgent(): String {
        return "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Ubuntu Chromium/70.0.3538.77 Chrome/70.0.3538.77 Safari/537.36"
    }

    actual val uiDispatcher: CoroutineContext = Dispatchers.Main

    private fun fileDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(requireNotNull(documentDirectory).path)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    actual fun createClipEntry(text: String): ClipEntry {
        return ClipEntry.withPlainText(text)
    }

    actual fun shareText(text: String) {
        val textToShare = listOf(text)
        val activityViewController = UIActivityViewController(textToShare, null)
        UIApplication.sharedApplication.keyWindow
            ?.rootViewController
            ?.presentViewController(activityViewController, true, null)
    }

    fun decodeAvif(result: SourceFetchResult): DecodeResult? {
        val data = result.source.source().readByteArray()
        return IosAvifDecoder.create(bytes = data, threads = 1).use { decoder ->
            decoder.nextFrame()
            val frame = decoder.getFrame()
            frame.getBitmapResult().getOrNull()?.let {
                DecodeResult(image = it.asImage(), isSampled = false)
            }
        }
    }

    actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(Darwin) {
            block()
        }
    }

    actual suspend fun cleanCache(): Result<Boolean> {
        return Result.success(true)
    }
}