package com.xiaoyv.bangumi.shared.ui.component.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.BasicComponent as MiuixBasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults as MiuixBasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.preference.ArrowPreference as MiuixArrowPreference
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference as MiuixWindowDropdownPreference
import com.xiaoyv.bangumi.shared.ui.view.rememberMenuHapticFeedback
import top.yukonga.miuix.kmp.preference.SwitchPreference as MiuixSwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf

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
    if (isMiuixUi()) {
        MiuixSettingContainer(
            modifier = modifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            label = label,
            content = content,
        )
        return
    }

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
                .padding(horizontal = ContentMargin)
                .padding(top = ContentMargin, bottom = ContentMarginHalf),
            content = {
                CompositionLocalProvider(
                    value = LocalTextStyle provides MaterialTheme.typography.bodyMedium
                        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    content = label
                )
            }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }
}

@Composable
private fun MiuixSettingContainer(
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    label: @Composable (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        if (label != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin * 2)
                    .padding(top = ContentMargin, bottom = ContentMarginHalf),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium.copy(
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    ),
                    content = label,
                )
            }
        }
        MiuixCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentMargin)
                .padding(bottom = ContentMargin),
            insideMargin = PaddingValues(0.dp),
            content = content,
        )
    }
}

private val DefaultSettingTrailing: @Composable () -> Unit = { SettingItemTrailing() }

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    shape: ListItemShapes,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    leadingContent: @Composable (() -> Unit)? = icon?.let {
        {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    },
    trailingContent: @Composable (() -> Unit)? = DefaultSettingTrailing,
    supportingContent: @Composable (() -> Unit)? = null,
    divider: Boolean = false,
    colors: ListItemColors = ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ),
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit = {},
) {
    if (isMiuixUi()) {
        val titleColor = if (textStyle.color != Color.Unspecified) {
            MiuixBasicComponentDefaults.titleColor(color = textStyle.color)
        } else {
            MiuixBasicComponentDefaults.titleColor()
        }
        if (trailingContent === DefaultSettingTrailing && supportingContent == null) {
            MiuixArrowPreference(
                modifier = modifier,
                title = title,
                titleColor = titleColor,
                startAction = leadingContent,
                onClick = onClick,
                enabled = enabled,
            )
        } else {
            MiuixBasicComponent(
                modifier = modifier,
                title = title,
                titleColor = titleColor,
                startAction = leadingContent,
                endActions = trailingContent?.let { { it() } },
                bottomAction = supportingContent,
                onClick = onClick,
                enabled = enabled,
            )
        }
        if (divider) BgmHorizontalDivider()
        return
    }

    SegmentedListItem(
        modifier = Modifier
            .padding(vertical = 1.dp)
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick,
        shapes = shape,
        colors = colors,
        enabled = enabled,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        supportingContent = supportingContent,
        content = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = textStyle,
            )
        },
    )
    if (divider) {
        BgmHorizontalDivider()
    }
}

@Composable
fun <T : Any> SettingOptionItem(
    title: String,
    shape: ListItemShapes,
    description: String? = null,
    value: String,
    items: SerializeList<ComposeTextTab<T>>,
    onClick: (T) -> Unit,
) {
    if (isMiuixUi()) {
        val labels = items.map { it.displayText() }
        MiuixWindowDropdownPreference(
            title = title,
            summary = description,
            items = labels,
            selectedIndex = labels.indexOf(value),
            onSelectedIndexChange = { index ->
                items.getOrNull(index)?.let { onClick(it.type) }
            },
        )
        return
    }

    val dialogState = rememberAlertDialogState()
    val openingFeedback = rememberMenuHapticFeedback()

    AlertOptionDialog(
        title = title,
        message = description,
        state = dialogState,
        items = items,
        onClick = { tab, _ ->
            onClick(tab.type)
        }
    )

    SettingItem(
        title = title,
        shape = shape,
        trailingContent = { SettingItemTrailing(text = value) },
        onClick = {
            openingFeedback()
            dialogState.show()
        }
    )
}

@Composable
fun SettingSwitchItem(
    title: String,
    shape: ListItemShapes,
    description: String? = null,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    if (isMiuixUi()) {
        MiuixSwitchPreference(
            checked = value,
            onCheckedChange = onValueChange,
            title = title,
            summary = description,
        )
        return
    }

    SettingItem(
        title = title,
        shape = shape,
        trailingContent = {
            Switch(
                checked = value,
                onCheckedChange = onValueChange
            )
        },
        supportingContent = if (description == null) null else {
            { Text(text = description) }
        },
        onClick = { onValueChange(!value) }
    )
}

@Composable
fun SettingInputItem(
    title: String,
    shape: ListItemShapes,
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
        shape = shape,
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
    if (isMiuixUi()) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!text.isNullOrBlank()) {
                MiuixText(
                    modifier = Modifier.widthIn(max = 120.dp),
                    text = text,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (imageVector != null) {
                MiuixIcon(
                    imageVector = imageVector,
                    contentDescription = text.orEmpty(),
                )
            }
        }
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!text.isNullOrBlank()) {
            Text(
                modifier = Modifier.widthIn(max = 120.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )
        }
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = text.orEmpty(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
            )
        }
    }
}
