/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("INVISIBLE_REFERENCE")
@file:OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.snizzors

import androidx.compose.runtime.CompositeKeyHashCode
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.asCGRect
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.unit.toDpRect
import androidx.compose.ui.unit.toRect
import androidx.compose.ui.viewinterop.InteropContainer
import androidx.compose.ui.viewinterop.InteropView
import androidx.compose.ui.viewinterop.InteropViewGroup
import androidx.compose.ui.viewinterop.InteropWrappingView
import androidx.compose.ui.viewinterop.TypedInteropViewHolder
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.nativeAccessibility
import androidx.compose.ui.viewinterop.pointerInteropFilter
import androidx.compose.ui.window.MetalView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRect
import platform.UIKit.UIView
import platform.UIKit.accessibilityFrame

internal abstract class SnizzorsInteropElementHolder<T : InteropView>(
    factory: () -> T,
    interopContainer: InteropContainer,
    private val interopWrappingView: InteropWrappingView,
    properties: UIKitInteropProperties,
    compositeKeyHashCode: CompositeKeyHashCode,
) : TypedInteropViewHolder<T>(
    factory,
    interopContainer,
    interopWrappingView,
    compositeKeyHashCode
) {
    constructor(
        factory: () -> T,
        interopContainer: InteropContainer,
        properties: UIKitInteropProperties,
        compositeKeyHashCode: CompositeKeyHashCode,
    ) : this(
        factory = factory,
        interopContainer = interopContainer,
        interopWrappingView = InteropWrappingView(
            interactionMode = null
        ),
        properties = properties,
        compositeKeyHashCode = compositeKeyHashCode
    )

    override val measurePolicy: MeasurePolicy = MeasurePolicy { _, constraints ->
        layout(constraints.minWidth, constraints.minHeight) {
            // No-op, no children are expected
            // TODO: attempt to calculate the size of the wrapped view using constraints
            //  and autolayout system if possible
            //  https://youtrack.jetbrains.com/issue/CMP-5873/iOS-investigate-intrinsic-sizing-of-interop-elements
        }
    }

    // TODO: no more clipping. rename/refactor?
    private var currentUnclippedRect: IntRect? = null
    private var currentClippedRect: IntRect? = null
    private var currentUserComponentRect: IntRect? = null

    var properties = properties
        set(value) {
            if (field != value) {
                field = value
                onPropertiesChanged()
            }
        }

    /**
     * Immediate frame of underlying user component. Can be different from
     * [currentUserComponentRect] due to scheduling.
     */
    protected abstract var userComponentCGRect: CValue<CGRect>

    init {
        onPropertiesChanged()
    }

    override fun layoutAccordingTo(layoutCoordinates: LayoutCoordinates) {
        val rootCoordinates = layoutCoordinates.findRootCoordinates()

        val unclippedRect = rootCoordinates
            .localBoundingBoxOf(
                sourceCoordinates = layoutCoordinates,
                clipBounds = false
            ).roundToIntRect()

        val clippedRect = rootCoordinates
            .localBoundingBoxOf(
                sourceCoordinates = layoutCoordinates,
                clipBounds = true
            ).roundToIntRect()

        if (currentUnclippedRect == unclippedRect && currentClippedRect == clippedRect) {
            return
        }

        // wrapping view itself is always using the clipped rect
        // don't issue a redundant update, if the clipped rect is the same
        if (clippedRect != currentClippedRect) {
            val groupFrame = clippedRect
                .toRect()
                .toDpRect(density)
                .asCGRect()
            val groupAccessibilityFrame = unclippedRect
                .toRect()
                .toDpRect(density)
                .asCGRect()

            container.scheduleUpdate {
                UIView.performWithoutAnimation {
                    group.setFrame(groupFrame)
                    group.accessibilityFrame = groupAccessibilityFrame
                }
            }
        }

        // user component is always updated if the unclipped or clipped rect changes,
        // because it needs to be moved inside the clipping view to keep the frame
        // in window coordinates the same
        if (currentUnclippedRect != unclippedRect || currentClippedRect != clippedRect) {
            // offset to move the component to the correct position inside the wrapping view, so
            // its root space frame stays the same if the wrapping view is clipped

            val userComponentRect = IntRect(
                offset = unclippedRect.topLeft - clippedRect.topLeft,
                size = unclippedRect.size
            )

            // update the user component frame only if it changes
            if (userComponentRect != currentUserComponentRect) {
                // Schedule frame update
                val newUserComponentCGRect =
                    userComponentRect
                        .toRect()
                        .toDpRect(density)
                        .asCGRect()

                container.scheduleUpdate {
                    UIView.performWithoutAnimation {
                        userComponentCGRect = newUserComponentCGRect
                    }
                }

                currentUserComponentRect = userComponentRect
            }
        }

        currentUnclippedRect = unclippedRect
        currentClippedRect = clippedRect
    }

    override fun dispatchToView(pointerEvent: PointerEvent) {
        // No-op, we can't dispatch events to UIView or UIViewController directly, see
        // [InteractionUIView] logic
    }

    override fun insertInteropView(root: InteropViewGroup, index: Int) {
        changeInteropViewIndex(root, index)
        super.insertInteropView(root, index)
    }

    override fun changeInteropViewIndex(root: InteropViewGroup, index: Int) {
        // Interop view gets inserted directly above the MetalView, which hosts the Compose content
        val superview = root.superview ?: return
        val metalViewIndex = superview.subviews.indexOfFirst { it is MetalView }
        superview.insertSubview(group, atIndex = (metalViewIndex + index + 1).toLong())
    }

    private fun onPropertiesChanged() {
        interopWrappingView.interactionMode = properties.interactionMode

        platformModifier = Modifier
            .pointerInteropFilter(
                isInteractive = properties.isInteractive,
                this
            )
            .nativeAccessibility(
                isEnabled = properties.isNativeAccessibilityEnabled,
                interopWrappingView
            )
    }
}