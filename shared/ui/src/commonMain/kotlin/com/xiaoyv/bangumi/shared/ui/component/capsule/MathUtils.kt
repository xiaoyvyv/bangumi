package com.xiaoyv.bangumi.shared.ui.component.capsule

@Suppress("NOTHING_TO_INLINE")
internal inline fun lerp(start: Double, stop: Double, fraction: Double): Double {
    return start + (stop - start) * fraction
}
