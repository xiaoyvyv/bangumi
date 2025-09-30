package com.xiaoyv.bangumi.shared.ui.component.emoji

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.xiaoyv.bangumi.shared.core.utils.bgmReactionKey
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import org.jetbrains.compose.resources.imageResource

@Composable
fun rememberPopupReactionState() = remember { PopupReactionState() }

@Stable
class PopupReactionState {
    internal var expanded by mutableStateOf(false)

    fun show() {
        expanded = true
    }

    fun dismiss() {
        expanded = false
    }
}

@Composable
fun PopupReaction(
    state: PopupReactionState,
    properties: PopupProperties = PopupProperties(focusable = true),
    onClick: (String) -> Unit = {},
) {
    DropdownMenu(
        expanded = state.expanded,
        onDismissRequest = { state.dismiss() },
        properties = properties,
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = LayoutPaddingHalf),
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            itemVerticalAlignment = Alignment.CenterVertically,
            maxItemsInEachRow = 4,
        ) {
            bgmReactionKey.forEach {
                IconButton(
                    modifier = Modifier.resetSize(),
                    onClick = {
                        onClick(it.key)
                        state.dismiss()
                    }
                ) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = BitmapPainter(
                            image = imageResource(it.value),
                            filterQuality = FilterQuality.None
                        ),
                        contentDescription = it.key,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}