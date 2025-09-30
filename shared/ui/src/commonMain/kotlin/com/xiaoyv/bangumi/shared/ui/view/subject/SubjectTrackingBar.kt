package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.subject_track_input_disable_tip
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneText
import org.jetbrains.compose.resources.stringResource

/**
 * 条目进度条
 *
 * @param onInputChangeConfirm 输入完成回调，为空时禁止点击输入进度
 * @param inputDisabledText 禁止手动输入进度时，点击的提示信息
 */
@Composable
fun SubjectTrackingBar(
    status: Int,
    total: Int,
    button: String,
    onClickIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    inputDisabledText: String = stringResource(Res.string.subject_track_input_disable_tip),
    onInputChangeConfirm: ((Int) -> Unit)? = null,
    width: Dp = 120.dp,
    strokeWidth: Dp = 28.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        val inputDialogState = rememberAlertInputDialogState()
        val popupTipState = LocalPopupTipState.current

        BgmAlertInputDialog(
            state = inputDialogState,
            onConfirm = {
                onInputChangeConfirm?.invoke(it.value.toIntOrNull() ?: 0)
            }
        )

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(strokeWidth)
                .border(ButtonDefaults.outlinedButtonBorder(), shape = MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = {
                    if (onInputChangeConfirm != null) inputDialogState.show {
                        it.copy(
                            title = "进度",
                            singleLine = true,
                            onlyNumber = true,
                            value = status.toString(),
                        )
                    } else {
                        popupTipState.showToast(inputDisabledText)
                    }
                }),
            contentAlignment = Alignment.CenterStart
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight(),
                gapSize = 0.dp,
                color = colorCollectionDoneContainer,
                trackColor = colorCollectionDoneContainer.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Square,
                drawStopIndicator = {},
                progress = {
                    when {
                        total > 0 -> status / total.toFloat()
                        status > 0 -> 1f
                        else -> 0f
                    }
                }
            )
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = "${status}/${if (total == 0) "*" else total}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorCollectionDoneText,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Box(
            modifier = Modifier
                .padding(start = LayoutPadding)
                .height(strokeWidth)
                .border(ButtonDefaults.outlinedButtonBorder(), shape = MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onClickIncrease)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = button,
                maxLines = 1,
                modifier = Modifier.basicMarquee(Int.MAX_VALUE, spacing = MarqueeSpacing(LayoutPaddingHalf)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}