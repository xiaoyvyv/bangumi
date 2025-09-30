package com.xiaoyv.bangumi.shared.core.utils


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyLayoutScrollScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.layout.LazyLayoutScrollScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs

private val TargetDistance = 2500.dp
private val BoundDistance = 1500.dp
private val MinimumDistance = 50.dp

private class ItemFoundInScroll(
    val itemOffset: Int,
    val previousAnimation: AnimationState<Float, AnimationVector1D>,
) : CancellationException()

internal fun LazyLayoutScrollScope.isItemVisible(index: Int): Boolean {
    return index in firstVisibleItemIndex..lastVisibleItemIndex
}

suspend fun LazyListState.animateScrollToItem(
    index: Int,
    scrollOffset: Int = 0,
    density: Density,
    animationSpec: AnimationSpec<Float> = tween(500),
) {
    scroll {
        LazyLayoutScrollScope(this@animateScrollToItem, this)
            .animateScrollToItem(index, scrollOffset, 100, density, animationSpec)
    }
}

suspend fun LazyLayoutScrollScope.animateScrollToItem(
    index: Int,
    scrollOffset: Int,
    numOfItemsForTeleport: Int,
    density: Density,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    require(index >= 0f) { "Index should be non-negative" }

    try {
        val targetDistancePx = with(density) { TargetDistance.toPx() }
        val boundDistancePx = with(density) { BoundDistance.toPx() }
        val minDistancePx = with(density) { MinimumDistance.toPx() }
        var loop = true
        var anim = AnimationState(0f)

        if (isItemVisible(index)) {
            val targetItemInitialOffset = calculateDistanceTo(index)
            // It's already visible, just animate directly
            throw ItemFoundInScroll(targetItemInitialOffset, anim)
        }
        val forward = index > firstVisibleItemIndex

        fun isOvershot(): Boolean {
            // Did we scroll past the item?
            @Suppress("RedundantIf") // It's way easier to understand the logic this way
            return if (forward) {
                if (firstVisibleItemIndex > index) {
                    true
                } else if (
                    firstVisibleItemIndex == index && firstVisibleItemScrollOffset > scrollOffset
                ) {
                    true
                } else {
                    false
                }
            } else { // backward
                if (firstVisibleItemIndex < index) {
                    true
                } else if (
                    firstVisibleItemIndex == index && firstVisibleItemScrollOffset < scrollOffset
                ) {
                    true
                } else {
                    false
                }
            }
        }

        var loops = 1
        while (loop && itemCount > 0) {
            val expectedDistance = calculateDistanceTo(index) + scrollOffset
            val target =
                if (abs(expectedDistance) < targetDistancePx) {
                    val absTargetPx = maxOf(abs(expectedDistance.toFloat()), minDistancePx)
                    if (forward) absTargetPx else -absTargetPx
                } else {
                    if (forward) targetDistancePx else -targetDistancePx
                }

            anim = anim.copy(value = 0f)
            var prevValue = 0f
            anim.animateTo(target, sequentialAnimation = (anim.velocity != 0f), animationSpec = animationSpec) {
                if (!isItemVisible(index)) {
                    // Springs can overshoot their target, clamp to the desired range
                    val coercedValue = if (target > 0) {
                        value.coerceAtMost(target)
                    } else {
                        value.coerceAtLeast(target)
                    }
                    val delta = coercedValue - prevValue

                    val consumed = scrollBy(delta)
                    if (!isItemVisible(index)) {
                        if (!isOvershot()) {
                            if (delta != consumed) {
                                cancelAnimation()
                                loop = false
                                return@animateTo
                            }
                            prevValue += delta
                            if (forward) {
                                if (value > boundDistancePx) {
                                    cancelAnimation()
                                }
                            } else {
                                if (value < -boundDistancePx) {
                                    cancelAnimation()
                                }
                            }

                            if (forward) {
                                if (loops >= 2 && index - lastVisibleItemIndex > numOfItemsForTeleport) {
                                    // Teleport
                                    snapToItem(index = index - numOfItemsForTeleport, offset = 0)
                                }
                            } else {
                                if (loops >= 2 && firstVisibleItemIndex - index > numOfItemsForTeleport) {
                                    // Teleport
                                    snapToItem(index = index + numOfItemsForTeleport, offset = 0)
                                }
                            }
                        }
                    }
                }

                // We don't throw ItemFoundInScroll when we snap, because once we've snapped to
                // the final position, there's no need to animate to it.
                if (isOvershot()) {
                    snapToItem(index = index, offset = scrollOffset)
                    loop = false
                    cancelAnimation()
                    return@animateTo
                } else if (isItemVisible(index)) {
                    val targetItemOffset = calculateDistanceTo(index)
                    throw ItemFoundInScroll(targetItemOffset, anim)
                }
            }

            loops++
        }
    } catch (itemFound: ItemFoundInScroll) {
        // We found it, animate to it
        // Bring to the requested position - will be automatically stopped if not possible
        val anim = itemFound.previousAnimation.copy(value = 0f)
        val target = (itemFound.itemOffset + scrollOffset).toFloat()
        var prevValue = 0f
        anim.animateTo(target, sequentialAnimation = (anim.velocity != 0f), animationSpec = animationSpec) {
            // Springs can overshoot their target, clamp to the desired range
            val coercedValue =
                when {
                    target > 0 -> value.coerceAtMost(target)
                    target < 0 -> value.coerceAtLeast(target)
                    else -> 0f
                }
            val delta = coercedValue - prevValue
            val consumed = scrollBy(delta)
            if (
                delta != consumed /* hit the end, stop */ ||
                coercedValue != value /* would have overshot, stop */
            ) {
                cancelAnimation()
            }
            prevValue += delta
        }
        // Once we're finished the animation, snap to the exact position to account for
        // rounding error (otherwise we tend to end up with the previous item scrolled the
        // tiniest bit onscreen)
        snapToItem(index = index, offset = scrollOffset)
    }
}