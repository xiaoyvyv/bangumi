package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.TagCode
import com.xiaoyv.bangumi.shared.core.utils.applyTheme
import com.xiaoyv.bangumi.shared.core.utils.awaitHtmlEvent
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.ui.component.action.AppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import kotlinx.collections.immutable.toImmutableMap

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
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    inlineContent: SerializeMap<String, InlineTextContent> = InlineTextContentMap,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    actionHandler: AppActionHandler = LocalActionHandler.current,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClickLink: (AnnotatedString.Range<String>) -> Unit = {
        actionHandler.openBgmLink(it.item)
    },
    onClickImage: (AnnotatedString.Range<String>) -> Unit = {
        actionHandler.openImage(it.item)
    },
) {
    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
        val aspect = 16 / 9f
        val width = with(density) {
            maxWidth.coerceAtMost(450.dp).toSp()
        }

        val newInlineContent = remember(inlineContent, width) {
            val tmp = inlineContent.toMutableMap()
            tmp["image"] = createImageInlineContent(
                width = width,
                height = width / aspect
            )
            tmp.toImmutableMap()
        }

        val showMasks: MutableSet<AnnotatedString.Range<String>> = remember { mutableStateSetOf() }
        val content = text.applyTheme(
            showMaskRanges = showMasks,
            contentColor = style.color
        )

        // 代码块
        val codeCornerRadius = with(density) { 4.dp.toPx() }
        val cachedRects = remember(text, layoutResult) {
            layoutResult?.let { result ->
                text.getStringAnnotations(TagCode, 0, text.length).map { annotation ->
                    val startLine = result.getLineForOffset(annotation.start)
                    val endLine = result.getLineForOffset(annotation.end)

                    val top = result.getLineTop(startLine)
                    val bottom = result.getLineBottom(endLine)

                    // 块级背景铺满整行宽度
                    val left = 0f
                    val right = result.size.width.toFloat()

                    androidx.compose.ui.geometry.Rect(
                        left,
                        top - codeCornerRadius,
                        right,
                        bottom + codeCornerRadius
                    )
                }
            } ?: emptyList()
        }


        BasicText(
            text = content,
            modifier = Modifier
                .drawBehind {
                    cachedRects.forEach { rect ->
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.75f),
                            topLeft = Offset(rect.left, rect.top),
                            size = androidx.compose.ui.geometry.Size(rect.width, rect.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(codeCornerRadius),
                        )
                    }
                }
                .pointerInput(onClickLink) {
                    awaitHtmlEvent(
                        text = content,
                        onTextLayoutResult = { layoutResult },
                        onClickLink = onClickLink,
                        onClickImage = onClickImage,
                        onClickMask = { range -> showMasks.add(range) }
                    )
                },
            style = style.copy(
                color = if (style.color == Color.Unspecified) LocalContentColor.current else style.color,
                lineHeight = style.fontSize * 1.75f
            ),
            softWrap = softWrap,
            inlineContent = newInlineContent,
            overflow = overflow,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = {
                layoutResult = it
                onTextLayout(it)
            }
        )
    }
}