package com.xiaoyv.bangumi.shared.ui.component.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin


@Composable
fun PopupTipContent(data: SnackbarData) {
    Box(Modifier.padding(32.dp)) {
        Surface(
            shape = MaterialTheme.shapes.largeIncreased,
            color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shadowElevation = 8.dp,
        ) {
            Text(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .padding(ContentMargin),
                text = data.visuals.message,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}