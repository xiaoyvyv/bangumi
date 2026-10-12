package com.xiaoyv.bangumi.shared.ui.component.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unselected
import com.xiaoyv.bangumi.core_resource.resources.global_more
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.MiuixActionMenu
import com.xiaoyv.bangumi.shared.ui.view.rememberMenuHapticFeedback
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.menu.WindowIconDropdownMenu
import top.yukonga.miuix.kmp.popup.WindowDropdownPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText

@Composable
fun <T : Any> DropMenuActionButton(
    options: SerializeList<ComposeTextTab<T>>,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = BgmIcons.MoreVert,
    imageTint: Color = LocalContentColor.current,
    current: T? = null,
    onOptionClick: (ComposeTextTab<T>) -> Unit = {},
) {
    val openingFeedback = rememberMenuHapticFeedback()
    val selectionFeedback = rememberMenuHapticFeedback(HapticFeedbackType.Confirm)
    if (isMiuixUi()) {
        if (options.any { it.type == ButtonType.Delete || it.type == ButtonType.Report || it.type == ButtonType.Block }) {
            MiuixActionMenu(options, imageVector, imageTint, modifier, current, onOptionClick)
            return
        }
        WindowIconDropdownMenu(
            modifier = modifier,
            entry = DropdownEntry(
                items = options.map { tab ->
                    DropdownItem(
                        text = tab.displayText(),
                        selected = tab.type == current,
                        onClick = { onOptionClick(tab) },
                    )
                },
            ),
        ) {
            MiuixIcon(
                imageVector = imageVector,
                contentDescription = stringResource(Res.string.global_more),
                tint = imageTint,
            )
        }
        return
    }

    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }

        IconButton(
            modifier = modifier,
            onClick = {
                openingFeedback()
                expanded = true
            }
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(Res.string.global_more),
                tint = imageTint
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.fastForEach {
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors(
                        textColor = if (it.contentColor != Color.Unspecified) it.contentColor else LocalContentColor.current
                    ),
                    text = { Text(text = it.displayText()) },
                    onClick = {
                        selectionFeedback()
                        expanded = false
                        onOptionClick(it)
                    }
                )
            }
        }
    }
}

@Composable
fun <T : Any> DropMenuChip(
    options: SerializeList<ComposeTextTab<T>>,
    current: T? = options.firstOrNull()?.type,
    labelPrefix: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: ChipColors = AssistChipDefaults.assistChipColors(
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        trailingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    border: BorderStroke? = AssistChipDefaults.assistChipBorder(true),
    onOptionClick: (ComposeTextTab<T>) -> Unit = {},
) {
    val openingFeedback = rememberMenuHapticFeedback()
    val selectionFeedback = rememberMenuHapticFeedback(HapticFeedbackType.Confirm)
    if (isMiuixUi()) {
        Box {
            var expanded by rememberSaveable { mutableStateOf(false) }
            val target = options.find { it.type == current }
            val label = buildString {
                if (labelPrefix != null) append(labelPrefix)
                if (target != null) {
                    if (labelPrefix != null) append(" ")
                    append(target.displayText())
                } else {
                    append(stringResource(Res.string.global_unselected))
                }
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MiuixTheme.colorScheme.surfaceContainerHigh)
                    .clickable {
                        openingFeedback()
                        expanded = true
                    }
                    .padding(horizontal = ContentMarginHalf * 2, vertical = ContentMarginHalf),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                MiuixText(
                    text = label,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                trailingIcon?.invoke()
            }

            WindowDropdownPopup(
                entry = DropdownEntry(
                    items = options.map { tab ->
                        DropdownItem(
                            text = tab.displayText(),
                            selected = tab.type == current,
                            onClick = { onOptionClick(tab) },
                        )
                    },
                ),
                show = expanded,
                onDismiss = { expanded = false },
                onDismissFinished = {},
                maxHeight = null,
                dropdownColors = DropdownDefaults.dropdownColors(),
                collapseOnSelection = true,
            )
        }
        return
    }

    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }

        AssistChip(
            onClick = {
                openingFeedback()
                expanded = true
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = colors,
            border = border,
            label = {
                val target = options.find { it.type == current }
                Text(
                    text = buildString {
                        if (labelPrefix != null) append(labelPrefix)
                        if (target != null) {
                            if (labelPrefix != null) append(" ")
                            append(target.displayText())
                        } else {
                            append(stringResource(Res.string.global_unselected))
                        }
                    }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.fastForEach {
                DropdownMenuItem(
                    text = { Text(text = it.displayText()) },
                    onClick = {
                        selectionFeedback()
                        expanded = false
                        onOptionClick(it)
                    }
                )
            }
        }
    }
}
