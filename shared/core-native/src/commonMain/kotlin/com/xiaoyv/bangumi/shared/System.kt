package com.xiaoyv.bangumi.shared

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.xiaoyv.bangumi.shared.native.AppDatabase
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import kotlin.coroutines.CoroutineContext

val systemDevice by lazy { SystemDevice() }

expect object System {
    val isDebugType: Boolean
    val uiDispatcher: CoroutineContext
    val database: AppDatabase
    val datastore: DataStore<Preferences>

    suspend fun cleanCache(): Result<Boolean>

    fun createClipEntry(text: String): ClipEntry

    fun userAgent(): String

    fun currentTimeMillis(): Long

    fun launchDeeplinkSettings()

    fun log(tag: String, message: String)

    fun shareText(text: String)

    fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient
}