package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.xiaoyv.bangumi.shared.ui.view.rememberMenuHapticFeedback
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.Check
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import top.yukonga.miuix.kmp.window.WindowDialog
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.verticalScroll

/**
 * [AlertOptionDialog]
 *
 * @param current 当前选中值，非选择型操作列表可以省略。
 *
 * @since 2025/5/15
 */
@Composable
fun <Key : Any> AlertOptionDialog(
    state: AlertDialogState,
    title: String?,
    message: String? = null,
    items: SerializeList<ComposeTextTab<Key>>,
    current: Key? = null,
    onClick: (ComposeTextTab<Key>, Int) -> Unit,
) {
    val selectionFeedback = rememberMenuHapticFeedback(HapticFeedbackType.Confirm)
    if (isMiuixUi()) {
        WindowDialog(
            show = state.showing,
            title = title,
            summary = message,
            onDismissRequest = { state.dismiss() },
        ) {
            Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                items.fastForEachIndexed { index, item ->
                    BasicComponent(
                        modifier = Modifier.semantics { if (current != null) selected = item.type == current },
                        title = item.displayText(),
                        titleColor = BasicComponentDefaults.titleColor(
                            color = if (item.type == current) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.onSurface,
                        ),
                        endActions = {
                            if (item.type == current) {
                                MiuixIcon(
                                    imageVector = MiuixIcons.Basic.Check,
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.primary,
                                )
                            }
                        },
                        onClick = {
                            selectionFeedback()
                            onClick(item, index)
                            state.dismiss()
                        },
                    )
                }
            }
        }
        return
    }
    if (state.showing) BasicAlertDialog(onDismissRequest = { state.dismiss() }) {
        BgmAppTheme(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clip(AlertDialogDefaults.shape)
                .background(AlertDialogDefaults.containerColor)
                .padding(top = 12.dp, bottom = 20.dp),
            containerColor = null
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (title != null) Text(
                    modifier = Modifier.padding(
                        vertical = ContentMargin,
                        horizontal = 20.dp
                    ),
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                if (message != null) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = ContentMarginHalf),
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items.fastForEachIndexed { i, tab ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectionFeedback()
                                onClick(tab, i)
                                state.dismiss()
                            }
                            .padding(horizontal = 20.dp, vertical = ContentMargin),
                        text = tab.displayText(),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AlertContentDialog(
    state: AlertDialogState,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    if (isMiuixUi() && properties.dismissOnBackPress && properties.dismissOnClickOutside) {
        WindowDialog(
            show = state.showing,
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
            content = content,
        )
        return
    }
    if (state.showing) BasicAlertDialog(
        modifier = modifier,
        properties = properties,
        onDismissRequest = { state.dismiss() }
    ) {
        BgmAppTheme(
            modifier = Modifier
                .clip(AlertDialogDefaults.shape)
                .background(AlertDialogDefaults.containerColor)
        ) {
            content()
        }
    }
}
