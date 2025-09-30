package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_more_tag
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import org.jetbrains.compose.resources.stringResource

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: String = "",
    fontWeight: FontWeight = FontWeight.Normal,
    showMore: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = fontWeight
            )
            if (!subtitle.isNullOrBlank()) Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showMore) Row(
            modifier = Modifier.offset(x = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (action.isNotBlank()) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Icon(
                imageVector = BgmIconsMirrored.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.global_more_tag),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}