package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.animation.core.AnimationConstants
import com.xiaoyv.bangumi.shared.System

private val lastClickMap = HashMap<String, Long>()

fun debounce(
    key: String,
    duration: Int = AnimationConstants.DefaultDurationMillis,
    block: () -> Unit,
) {
    val currentTime = System.currentTimeMillis()
    val lastClickTime = lastClickMap[key] ?: 0L
    val diff = currentTime - lastClickTime
    if (diff >= duration) {
        lastClickMap[key] = currentTime
        block()
    }
}