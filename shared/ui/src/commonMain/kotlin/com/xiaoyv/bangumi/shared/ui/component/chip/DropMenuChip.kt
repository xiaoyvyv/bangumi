package com.xiaoyv.bangumi.shared.ui.component.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.util.fastForEach
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unselected
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T : Any> DropMenuActionButton(
    options: SerializeList<ComposeTextTab<T>>,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = BgmIcons.MoreVert,
    imageTint: Color = LocalContentColor.current,
    onOptionClick: (ComposeTextTab<T>) -> Unit = {},
) {
    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }

        IconButton(
            modifier = modifier,
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
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
    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }

        AssistChip(
            onClick = { expanded = true },
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
                        expanded = false
                        onOptionClick(it)
                    }
                )
            }
        }
    }
}