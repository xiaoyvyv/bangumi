package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.shared.core.utils.TagImage
import com.xiaoyv.bangumi.shared.core.utils.TagMask
import com.xiaoyv.bangumi.shared.core.utils.applyTheme
import com.xiaoyv.bangumi.shared.core.utils.awaitHtmlEvent
import com.xiaoyv.bangumi.shared.core.utils.bbcodeToHtml
import com.xiaoyv.bangumi.shared.core.utils.packTextRangeKey
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.ui.component.action.AppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler

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
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    actionHandler: AppActionHandler = LocalActionHandler.current,
) {
    // 如果没有显式传文字颜色，则自动回退到 onSurface
    val resolvedTextStyle = textStyle.copy(
        color = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onSurface }
    )
    val html = remember(text) { if (text.contains("[")) bbcodeToHtml(text, true) else text }
    val parsed = remember(html) { html.parseAsHtml() }
    val targetShowMasks = remember { mutableStateSetOf<Long>() }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val aspect = 16f / 9f

        val imageWidth = with(density) { maxWidth.coerceAtMost(450.dp).toSp() }
        val imageHeight = (imageWidth.value / aspect).sp

        val inlineContent = remember(imageWidth, imageHeight) {
            InlineTextContentMap + mapOf(TagImage to createImageInlineContent(imageWidth, imageHeight))
        }

        val maskKeys = remember(parsed) {
            parsed.getStringAnnotations(TagMask, 0, parsed.length)
                .map { range -> packTextRangeKey(range.start, range.end) }
                .distinct()
                .sorted()
        }

        val maskAlphaByRange = buildMap {
            maskKeys.forEach { key ->
                val alpha by animateFloatAsState(
                    targetValue = if (targetShowMasks.contains(key)) 1f else 0f,
                    animationSpec = tween(220),
                    label = "mask-$key"
                )
                put(key, alpha)
            }
        }

        val content = parsed.applyTheme(
            contentColor = resolvedTextStyle.color,
            linkColor = MaterialTheme.colorScheme.primary,
            maskAlphaByRange = maskAlphaByRange
        )

        var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

        BasicText(
            text = content,
            modifier = Modifier.pointerInput(content, actionHandler) {
                awaitHtmlEvent(
                    text = content,
                    onTextLayoutResult = { layoutResult },
                    onClickLink = { range -> actionHandler.openBgmLink(range.item) },
                    onClickMask = { range ->
                        val key = packTextRangeKey(range.start, range.end)
                        if (!targetShowMasks.remove(key)) targetShowMasks.add(key)
                    },
                    onClickImage = { range -> actionHandler.openImage(range.item) },
                )
            },
            style = resolvedTextStyle,
            inlineContent = inlineContent,
            overflow = TextOverflow.Clip,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = { layoutResult = it }
        )
    }
}
