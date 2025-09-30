package com.xiaoyv.bangumi.shared.ui.component.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding

@Composable
fun DetailSectionTitle(
    title: String,
    action: String? = null,
    modifier: Modifier = Modifier,
    divider: Boolean = false,
    onActionClick: () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(LayoutPadding),
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(modifier = modifier, verticalArrangement = verticalArrangement) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LayoutPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (action != null) Text(
                modifier = Modifier.clickWithoutRipped(onClick = onActionClick),
                text = action,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (divider) BgmHorizontalDivider()
        if (content != null) content()
    }
}