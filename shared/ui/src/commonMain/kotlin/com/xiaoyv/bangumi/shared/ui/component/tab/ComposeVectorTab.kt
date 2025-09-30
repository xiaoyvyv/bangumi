package com.xiaoyv.bangumi.shared.ui.component.tab

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * [ComposeVectorTab]
 *
 * @author why
 * @since 2025/1/13
 */
@Immutable
data class ComposeVectorTab<T>(
    val type: T,
    val label: StringResource,
    val icon: ImageVector,
)

@Immutable
data class ComposeDrawableTab(
    val type: Any,
    val label: StringResource,
    val icon: DrawableResource,
)