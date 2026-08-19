package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.action.AppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.image.html.BgmHtmlImageContent
import com.xiaoyv.library.Html
import com.xiaoyv.library.HtmlDefaults

@Composable
fun textFieldTransparentColors() = TextFieldDefaults.colors(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
)

/**
 * [BmgTextField]
 *
 * @author why
 * @since 2025/1/25
 */
@Composable
fun BmgTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
    autoFocus: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    // If color is not provided via the text style, use content color as a default
    val textColor =
        textStyle.color.takeOrElse {
            val focused = interactionSource.collectIsFocusedAsState().value
            colors.textColor(enabled, isError, focused)
        }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    val focusRequester = remember { FocusRequester() }

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            modifier = modifier.focusRequester(focusRequester),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor(isError)),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox =
                @Composable { innerTextField ->
                    // places leading icon, text field with label and placeholder, trailing icon
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        label = label,
                        contentPadding = contentPadding,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        supportingText = supportingText,
                        shape = shape,
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors
                    )
                }
        )
    }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}


@Composable
fun BgmLinkedText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    actionHandler: AppActionHandler = LocalActionHandler.current,
) {
    // 如果没有显式传文字颜色，则自动回退到 onSurface
    val resolvedTextStyle = textStyle.copy(
        color = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onSurface }
    )

    Html(
        modifier = modifier,
        html = text,
        config = HtmlDefaults.config(
            textStyle = resolvedTextStyle,
            quoteTextStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            image = BgmHtmlImageContent,
            onClickImage = {
                if (it.smileId.isNullOrBlank()) {
                    actionHandler.openImage(it.source)
                }
            },
            onClickUrl = {
                actionHandler.openBgmLink(it)
            }
        )
    )
}
