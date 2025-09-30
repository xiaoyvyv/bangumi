package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlin.math.abs

private const val DefaultScrollMotionDurationScaleFactor = 2f
internal val DefaultScrollMotionDurationScale =
    object : MotionDurationScale {
        override val scaleFactor: Float
            get() = DefaultScrollMotionDurationScaleFactor
    }

interface ScrollableDefaultFlingBehavior : FlingBehavior {
    /**
     * Update the internal parameters of FlingBehavior in accordance with the new
     * [androidx.compose.ui.unit.Density] value.
     *
     * @param density new density value.
     */
    fun updateDensity(density: Density) = Unit
}

@Composable
fun rememberFlingBehavior(): FlingBehavior {
    val flingSpec = rememberSplineBasedDecay<Float>()
    return remember(flingSpec) { HeaderFlingBehavior(flingSpec) }
}

class HeaderFlingBehavior(
    private var flingDecay: DecayAnimationSpec<Float>,
    private val motionDurationScale: MotionDurationScale = DefaultScrollMotionDurationScale,
) : ScrollableDefaultFlingBehavior {

    // For Testing
    var lastAnimationCycleCount = 0

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        lastAnimationCycleCount = 0
        // come up with the better threshold, but we need it since spline curve gives us NaNs
        return withContext(motionDurationScale) {
            if (abs(initialVelocity) > 1f) {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                val animationState = AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity
                )
                try {
                    animationState.animateDecay(flingDecay) {
                        val delta = value - lastValue
                        val consumed = scrollBy(delta)
                        lastValue = value
                        velocityLeft = this.velocity
                        // avoid rounding errors and stop if anything is unconsumed
                        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                        lastAnimationCycleCount++
                    }
                } catch (_: CancellationException) {
                    velocityLeft = animationState.velocity
                }
                velocityLeft
            } else {
                initialVelocity
            }
        }
    }

    override fun updateDensity(density: Density) {
        flingDecay = splineBasedDecay(density)
    }
}
