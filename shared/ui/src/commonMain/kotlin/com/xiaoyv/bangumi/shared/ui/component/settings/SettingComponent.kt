package com.xiaoyv.bangumi.shared.ui.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin

/**
 * [SettingContainer]
 *
 * @author why
 * @since 2025/1/15
 */
@Composable
fun SettingContainer(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    label: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        if (label != null) Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = contentMargin)
                .padding(top = contentMargin, bottom = contentMargin / 2),
            content = {
                CompositionLocalProvider(
                    value = LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    content = { label() }
                )
            }
        )
        content()
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector? = null,
    leadingContent: @Composable (() -> Unit)? = icon?.let {
        {
            Icon(imageVector = icon, contentDescription = title)
        }
    },
    trailingContent: @Composable (() -> Unit)? = {
        SettingItemTrailing()
    },
    supportingContent: @Composable (() -> Unit)? = null,
    divider: Boolean = false,
    colors: ListItemColors = ListItemDefaults.colors(),
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = LayoutPaddingHalf / 2)
            .fillMaxWidth()
            .then(modifier),
        headlineContent = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = textStyle,
            )
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        supportingContent = supportingContent,
        colors = colors
    )
    if (divider) {
        BgmHorizontalDivider()
    }
}

@Composable
fun <T : Any> SettingOptionItem(
    title: String,
    value: String,
    items: SerializeList<ComposeTextTab<T>>,
    onClick: (T) -> Unit,
) {
    val dialogState = rememberAlertDialogState()

    AlertOptionDialog(
        title = title,
        state = dialogState,
        items = items,
        onClick = { tab, index ->
            onClick(tab.type)
        }
    )
    SettingItem(
        title = title,
        trailingContent = { SettingItemTrailing(text = value) },
        onClick = { dialogState.show() }
    )
}

@Composable
fun SettingSwitchItem(
    title: String,
    desc: String? = null,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    SettingItem(
        title = title,
        trailingContent = { Switch(checked = value, onCheckedChange = onValueChange) },
        supportingContent = if (desc == null) null else {
            { Text(text = desc) }
        },
        onClick = { onValueChange(!value) }
    )
}

@Composable
fun SettingInputItem(
    title: String,
    value: String,
    onClick: (String) -> Unit,
) {
    val dialogState = rememberAlertInputDialogState()

    BgmAlertInputDialog(
        state = dialogState,
        onConfirm = {
            onClick(it.value)
        }
    )

    SettingItem(
        title = title,
        trailingContent = { SettingItemTrailing(text = value) },
        onClick = { dialogState.show { it.copy(value = value, title = title) } }
    )
}

@Composable
fun SettingItemTrailing(
    modifier: Modifier = Modifier,
    text: String? = null,
    imageVector: ImageVector? = BgmIconsMirrored.KeyboardArrowRight,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!text.isNullOrBlank()) {
            Text(
                modifier = Modifier.widthIn(max = 120.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.MiddleEllipsis
            )
        }
        if (imageVector != null) {
            Icon(imageVector, text.orEmpty())
        }
    }
}