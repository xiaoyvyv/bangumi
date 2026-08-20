package com.xiaoyv.bangumi.shared

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.xiaoyv.bangumi.shared.native.AppDatabase
import io.github.vinceglb.filekit.PlatformFile
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import kotlin.coroutines.CoroutineContext

val systemDevice by lazy { SystemDevice() }
expect val platformContext: coil3.PlatformContext

expect object System {
    val isDebugType: Boolean
    val uiDispatcher: CoroutineContext
    val database: AppDatabase
    val datastore: DataStore<Preferences>

    suspend fun cleanCache(): Result<Boolean>

    suspend fun setWallpaper(file: PlatformFile)

    fun createClipEntry(text: String): ClipEntry

    fun userAgent(): String

    fun currentTimeMillis(): Long

    fun launchDeeplinkSettings()

    fun log(tag: String, message: String)

    fun shareText(text: String)

    fun createHttpClient(
        hosts: Map<String, List<String>>,
        tlsFragmentationDomains: Set<String> = hosts.keys,
        block: HttpClientConfig<*>.() -> Unit
    ): HttpClient
}
