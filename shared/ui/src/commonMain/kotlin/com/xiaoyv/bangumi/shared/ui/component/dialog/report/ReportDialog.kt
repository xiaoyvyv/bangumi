package com.xiaoyv.bangumi.shared.ui.component.dialog.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report
import com.xiaoyv.bangumi.core_resource.resources.action_report_attach
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_ad
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_argument
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_attack
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_illegal
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_irrelevant
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_other
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_political
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_privacy
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_score
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_spoiler
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.core.types.ReportValueType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private val reportOptions = persistentListOf(
    ComposeTextTab(ReportValueType.VALUE_8, Res.string.action_report_menu_ad),
    ComposeTextTab(ReportValueType.VALUE_7, Res.string.action_report_menu_argument),
    ComposeTextTab(ReportValueType.VALUE_1, Res.string.action_report_menu_attack),
    ComposeTextTab(ReportValueType.VALUE_2, Res.string.action_report_menu_irrelevant),
    ComposeTextTab(ReportValueType.VALUE_3, Res.string.action_report_menu_political),
    ComposeTextTab(ReportValueType.VALUE_4, Res.string.action_report_menu_illegal),
    ComposeTextTab(ReportValueType.VALUE_5, Res.string.action_report_menu_privacy),
    ComposeTextTab(ReportValueType.VALUE_6, Res.string.action_report_menu_score),
    ComposeTextTab(ReportValueType.VALUE_9, Res.string.action_report_menu_spoiler),
    ComposeTextTab(ReportValueType.VALUE_99, Res.string.action_report_menu_other)
)

@Composable
fun ReportDialog(
    state: AlertDialogState,
    options: SerializeList<ComposeTextTab<Int>> = reportOptions,
    onClick: (Int, String) -> Unit = { _, _ -> },
) {
    var selected by remember { mutableStateOf<ComposeTextTab<Int>?>(null) }
    var content by remember(Unit) { mutableStateOf(TextFieldValue()) }

    BgmAlertDialog(
        state = state,
        title = { Text(text = stringResource(Res.string.action_report)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(LayoutPadding)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEach { tab ->
                        FilterChip(
                            selected = tab == selected,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (tab == selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainer
                                },
                                labelColor = if (tab == selected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ),
                            border = if (tab == selected) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            },
                            onClick = { selected = tab },
                            label = { Text(tab.displayText()) }
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = content,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = Color.Unspecified,
                        unfocusedContainerColor = Color.Unspecified,
                    ),
                    label = { Text(text = stringResource(Res.string.action_report_attach)) },
                    maxLines = 3,
                    minLines = 3,
                    onValueChange = { content = it }
                )
            }
        },
        confirm = {
            TextButton(
                enabled = selected != null,
                onClick = {
                    state.dismiss()
                    onClick(requireNotNull(selected).type, content.text.trim())
                },
                content = { Text(stringResource(Res.string.global_confirm)) }
            )
        },
        cancel = {
            TextButton(
                onClick = { state.dismiss() },
                content = { Text(stringResource(Res.string.global_cancel)) }
            )
        }
    )
}