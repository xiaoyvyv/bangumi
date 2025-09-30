package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset


fun Modifier.resetSize(): Modifier {
    return defaultMinSize(1.dp, 1.dp)
}

inline fun <T> Modifier.noNull(
    data: T?,
    block: Modifier.(T) -> Modifier,
): Modifier = if (data != null) block(data) else this


fun Modifier.clickWithoutRipped(onClick: () -> Unit): Modifier {
    return clickable(
        indication = null,
        interactionSource = null,
        onClick = onClick
    )
}

fun Modifier.ignoreLazyGridContentPadding(horizontalPadding: Dp): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val offsetPx = (horizontalPadding * 2).roundToPx()
        val looseConstraints = constraints.offset(offsetPx, 0)
        val placeable = measurable.measure(looseConstraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
)
