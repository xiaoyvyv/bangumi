package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.xiaoyv.bangumi.shared.core.utils.bbcodeToHtml
import com.xiaoyv.bangumi.shared.ui.component.action.AppActionHandler
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.library.Html

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
    Html(
        modifier = modifier,
        html = remember(text) { if (text.contains("[")) bbcodeToHtml(text, true) else text },
        textStyle = resolvedTextStyle,
        onClickUrl = { actionHandler.openBgmLink(it) },
        inlineImage = {
            StateImage(
                model = it.source,
                modifier = Modifier.fillMaxSize(),
                contentDescription = it.alt
            )
        },
        blockImage = {
            StateImage(
                model = it.source,
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { actionHandler.openImage(it.source) },
                shape = MaterialTheme.shapes.small,
                alignment = Alignment.CenterStart,
                contentDescription = it.alt
            )
        }
    )


//    BbCode(
//        modifier = modifier,
//        source = if (text.contains("</")) Html2Bbcode.convert(text) else text,
//        textStyle = textStyle,
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        onLinkClick = { actionHandler.openBgmLink(it) },
//        imageRenderer = {
//            StateImage(
//                model = it.url,
//                modifier = Modifier
//                    .widthIn(max = 500.dp)
//                    .fillMaxWidth()
//                    .aspectRatio(4 / 3f)
//                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
//                    .clip(MaterialTheme.shapes.small)
//                    .clickable { actionHandler.openImage(it.url) },
//                shape = MaterialTheme.shapes.small,
//                alignment = Alignment.CenterStart
//            )
//        }
//    )

    /*
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
    }*/
}