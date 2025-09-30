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
@file:OptIn(ExperimentalForeignApi::class)

package com.xiaoyv.bangumi.shared.snizzors

import androidx.compose.runtime.CompositeKeyHashCode
import androidx.compose.ui.viewinterop.InteropContainer
import androidx.compose.ui.viewinterop.InteropViewGroup
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRect
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController
import platform.UIKit.removeFromParentViewController
import platform.UIKit.willMoveToParentViewController

internal class SnizzorsInteropViewControllerHolder<T : UIViewController>(
    factory: () -> T,
    interopContainer: InteropContainer,
    private val parentViewController: UIViewController,
    properties: UIKitInteropProperties,
    compositeKeyHashCode: CompositeKeyHashCode,
) : SnizzorsInteropElementHolder<T>(
    factory,
    interopContainer,
    properties,
    compositeKeyHashCode
) {
    init {
        // Group will be placed to hierarchy in [InteropContainer.placeInteropView]
        group.addSubview(interopView.view)
    }

    override var userComponentCGRect: CValue<CGRect>
        get() = interopView.view.frame
        set(value) {
            interopView.view.setFrame(value)
        }

    override fun insertInteropView(root: InteropViewGroup, index: Int) {
        parentViewController.addChildViewController(interopView)
        root.insertSubview(group, index.toLong())
        interopView.didMoveToParentViewController(parentViewController)

        super.insertInteropView(root, index)
    }

    override fun removeInteropView(root: InteropViewGroup) {
        interopView.willMoveToParentViewController(null)
        group.removeFromSuperview()
        interopView.removeFromParentViewController()

        super.removeInteropView(root)
    }
}