package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import com.xiaoyv.bangumi.shared.ui.theme.contentMarginHalf

/**
 * [AlertOptionDialog]
 *
 * @since 2025/5/15
 */
@Composable
fun <Key : Any> AlertOptionDialog(
    state: AlertDialogState,
    title: String,
    message: String? = null,
    items: SerializeList<ComposeTextTab<Key>>,
    onClick: (ComposeTextTab<Key>, Int) -> Unit,
) {
    if (state.showing) BasicAlertDialog(onDismissRequest = { state.dismiss() }) {
        BgmAppTheme(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clip(AlertDialogDefaults.shape)
                .background(AlertDialogDefaults.containerColor)
                .padding(top = 12.dp, bottom = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(contentMargin),
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                if (message != null) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = contentMargin)
                            .padding(vertical = contentMarginHalf),
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items.fastForEachIndexed { i, tab ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick(tab, i)
                                state.dismiss()
                            }
                            .padding(horizontal = contentMargin, vertical = contentMargin),
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
