package com.xiaoyv.bangumi.shared.component

import androidx.annotation.IntDef
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

@IntDef(
    DetectType.CHARACTER,
    DetectType.SOURCE
)
@Retention(AnnotationRetention.SOURCE)
annotation class DetectType {
    companion object {
        const val SOURCE = 1
        const val CHARACTER = 2
    }
}

/**
 * ExternalUriHandler
 */
object ExternalUriHandler {
    /**
     * Storage for when a URI arrives before the listener is set up
     */
    private val cached = atomic<Set<Pair<String, Int?>>>(emptySet())

    var onOpenShareImageListener: ((String, Int?) -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {
                cached.value.forEach { value.invoke(it.first, it.second) }
                cached.update { emptySet() }
            }
        }


    /**
     * When a new URI arrives, cache it.
     * If the listener is already set, invoke it and clear the cache immediately.
     */
    fun onImageReceived(path: String) = onImageReceived(path, detectType = null)

    fun onImageReceived(path: String, @DetectType detectType: Int? = null) {
        cached.update { it + (path to detectType) }
        onOpenShareImageListener?.let {
            it.invoke(path, detectType)
            cached.update { emptySet() }
        }
    }
}

@Composable
fun LaunchReceiveShareImageEffect(
    enable: Boolean,
    onReceiveImagePath: (String, Int?) -> Unit,
) {
    if (enable) DisposableEffect(onReceiveImagePath) {
        ExternalUriHandler.onOpenShareImageListener = onReceiveImagePath
        onDispose {
            ExternalUriHandler.onOpenShareImageListener = null
        }
    }
}