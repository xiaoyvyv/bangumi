package com.xiaoyv.bangumi.shared.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalContentColor
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.ButtonColors as MiuixButtonColors
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator as MiuixProgressIndicator
import top.yukonga.miuix.kmp.basic.ProgressIndicatorDefaults as MiuixProgressIndicatorDefaults
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixContentColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    if (isMiuixUi()) {
        MiuixLoadingButton(onClick, modifier, enabled, loading, colors, contentPadding, interactionSource, content)
        return
    }
    Button(
        onClick = { if (!loading) onClick() },
        modifier = modifier,
        enabled = enabled && !loading,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = colors.contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    )
}

@Composable
fun LoadingTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = ButtonDefaults.textShape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    if (isMiuixUi()) {
        MiuixLoadingButton(onClick, modifier, enabled, loading, colors, contentPadding, interactionSource, content)
        return
    }
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !loading,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = colors.contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    )
}

@Composable
fun LoadingIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    if (isMiuixUi()) {
        val active = enabled && !loading
        val foreground = if (active) colors.contentColor else colors.disabledContentColor
        MiuixIconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = active,
            backgroundColor = if (active) colors.containerColor else colors.disabledContainerColor,
        ) {
            CompositionLocalProvider(LocalContentColor provides foreground, MiuixContentColor provides foreground) {
                if (loading) {
                    MiuixProgressIndicator(
                        size = 16.dp,
                        strokeWidth = 2.dp,
                        colors = MiuixProgressIndicatorDefaults.progressIndicatorColors(foregroundColor = foreground),
                    )
                } else content()
            }
        }
        return
    }
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !loading,
        colors = colors,
        interactionSource = interactionSource,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = colors.contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    )
}

@Composable
private fun MiuixLoadingButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    colors: ButtonColors,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?,
    content: @Composable RowScope.() -> Unit,
) {
    val active = enabled && !loading
    MiuixButton(
        onClick = onClick,
        modifier = modifier,
        enabled = active,
        colors = MiuixButtonColors(
            color = colors.containerColor,
            contentColor = colors.contentColor,
            disabledColor = colors.disabledContainerColor,
            disabledContentColor = colors.disabledContentColor,
        ),
        insideMargin = contentPadding,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(LocalContentColor provides MiuixContentColor.current) {
            if (loading) {
                MiuixProgressIndicator(
                    size = 16.dp,
                    strokeWidth = 2.dp,
                    colors = MiuixProgressIndicatorDefaults.progressIndicatorColors(foregroundColor = MiuixContentColor.current),
                )
            } else content()
        }
    }
}
