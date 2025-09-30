package com.xiaoyv.bangumi.shared.ui.view.tag

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList

@Composable
fun TagItems(
    tags: SerializeList<String>,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
    onClick: (String) -> Unit = {},
) {
    tags.fastForEach {
        TagItem(
            tag = it,
            shape = shape,
            color = color,
            contentPadding = contentPadding,
            onClick = onClick
        )
    }
}

@Composable
fun TagItem(
    tag: String,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
    onClick: (String) -> Unit = {},
) {
    Text(
        modifier = Modifier
            .clip(shape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape)
            .clickable(onClick = { onClick(tag) })
            .padding(contentPadding),
        text = tag,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}