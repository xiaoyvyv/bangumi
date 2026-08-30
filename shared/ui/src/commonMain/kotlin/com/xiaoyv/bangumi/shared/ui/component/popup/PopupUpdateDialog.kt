package com.xiaoyv.bangumi.shared.ui.component.popup

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.parseMarkdown
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_download
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeAppRelease
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

/**
 * 展示应用更新说明并提供下载入口。
 *
 * @param release 当前可用的应用发布信息。
 * @param state 弹窗的显示状态。
 * @param onDownload 点击下载后的回调。
 */
@Composable
fun PopupUpdateDialog(
    release: ComposeAppRelease,
    state: AlertDialogState,
    onDownload: (ComposeAppRelease) -> Unit,
) {
    val scope = rememberCoroutineScope()
    if (state.showing && release != ComposeAppRelease.Empty) {
        AlertDialog(
            onDismissRequest = { state.dismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200.milliseconds)
                            onDownload(release)
                        }
                    },
                    content = { Text(stringResource(Res.string.global_download)) },
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { state.dismiss() },
                    content = { Text(stringResource(Res.string.global_cancel)) },
                )
            },
            title = { Text(release.tagName.uppercase()) },
            text = {
                Markdown(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    state = remember(release.body) { parseMarkdown(release.body) },
                    typography = markdownTypography(
                        h1 = MaterialTheme.typography.headlineLarge,
                        h2 = MaterialTheme.typography.headlineMedium,
                        h3 = MaterialTheme.typography.headlineSmall,
                        h4 = MaterialTheme.typography.titleLarge,
                        h5 = MaterialTheme.typography.titleMedium,
                        h6 = MaterialTheme.typography.titleSmall,
                    ),
                    colors = markdownColor(text = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            properties = state.properties,
        )
    }
}
