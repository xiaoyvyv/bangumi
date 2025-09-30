package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.emoji_normal
import com.xiaoyv.bangumi.core_resource.resources.emoji_tv
import com.xiaoyv.bangumi.core_resource.resources.emoji_tv_1
import com.xiaoyv.bangumi.core_resource.resources.emoji_tv_2
import com.xiaoyv.bangumi.core_resource.resources.global_preview
import com.xiaoyv.bangumi.core_resource.resources.reply_comment
import com.xiaoyv.bangumi.core_resource.resources.reply_comment_hint
import com.xiaoyv.bangumi.core_resource.resources.reply_comment_send
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.ImePanelColumn
import com.xiaoyv.bangumi.shared.core.utils.bbcodeBold
import com.xiaoyv.bangumi.shared.core.utils.bbcodeCode
import com.xiaoyv.bangumi.shared.core.utils.bbcodeEmpty
import com.xiaoyv.bangumi.shared.core.utils.bbcodeFontColor
import com.xiaoyv.bangumi.shared.core.utils.bbcodeFontSize
import com.xiaoyv.bangumi.shared.core.utils.bbcodeItalic
import com.xiaoyv.bangumi.shared.core.utils.bbcodeLink
import com.xiaoyv.bangumi.shared.core.utils.bbcodeMask
import com.xiaoyv.bangumi.shared.core.utils.bbcodeQuote
import com.xiaoyv.bangumi.shared.core.utils.bbcodeStrikethrough
import com.xiaoyv.bangumi.shared.core.utils.bbcodeToHtml
import com.xiaoyv.bangumi.shared.core.utils.bbcodeUnderline
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.insertEmoji
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.rememberBgmEmojis
import com.xiaoyv.bangumi.shared.core.utils.rememberImePanelState
import com.xiaoyv.bangumi.shared.core.utils.rememberTvEmojis
import com.xiaoyv.bangumi.shared.core.utils.rememberTvExtend1Emojis
import com.xiaoyv.bangumi.shared.core.utils.rememberTvExtend2Emojis
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.resource.toComposeUri
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.theme.BgmDefaultIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

val commentDialogProperties = DialogProperties(
    dismissOnBackPress = true,
    dismissOnClickOutside = true,
    usePlatformDefaultWidth = false,
)

@Composable
expect fun TransparentDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = commentDialogProperties,
    content: @Composable () -> Unit,
)

@Composable
fun CommentDialog(
    dialogState: AlertDialogState,
    anchor: CommentDialogAnchor,
    onSendCommentSuccess: (ComposeNewReply) -> Unit = {},
) {
    val showing by dialogState.showing.collectAsStateWithLifecycle()
    if (showing) TransparentDialog(
        onDismissRequest = { dialogState.dismiss() },
        properties = commentDialogProperties,
        content = {
            val viewModel = koinViewModel<CommentViewModel>(
                key = anchor.key,
                parameters = { parametersOf(anchor) }
            )
            val state by viewModel.collectAsState()

            viewModel.collectSideEffect {
                when (it) {
                    is CommentSideEffect.OnSendCommentSuccess -> {
                        onSendCommentSuccess(it.comment)
                        dialogState.dismiss()
                    }
                }
            }

            CommentDialogContent(
                state = state,
                dialogState = dialogState,
                onEvent = viewModel::onEvent
            )
        }
    )
}

@Composable
fun CommentDialogContent(
    state: CommentState,
    dialogState: AlertDialogState,
    onEvent: (CommentEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .weight(1f)
                .clickWithoutRipped { dialogState.dismiss() }
        )

        BgmHorizontalDivider()

        var showEmojiPanel by remember { mutableStateOf(false) }
        var showPreviewPanel by remember { mutableStateOf(false) }

        val panelState = rememberImePanelState(
            onResetPanel = {
                showEmojiPanel = false
                showPreviewPanel = false
            }
        )

        ImePanelColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            state = panelState,
            panelContent = {
                CommentDialogPanel(
                    modifier = Modifier.fillMaxSize(),
                    showEmojiPanel = showEmojiPanel,
                    showPreviewPanel = showPreviewPanel,
                    value = state.comment,
                    onValueChange = { onEvent(CommentEvent.OnTextChange(it)) }
                )
            }
        ) {
            val focusRequester = remember { FocusRequester() }
            val launcher = rememberFilePickerLauncher(FileKitType.Image) {
                if (it == null) {
                    focusRequester.requestFocus()
                } else {
                    onEvent(CommentEvent.OnImagePickResult(it))
                }
            }

            InputActionBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LayoutPaddingHalf),
                value = state.comment,
                onValueChange = { onEvent(CommentEvent.OnTextChange(it)) },
                onPickImage = { launcher.launch() },
                showEmojiPanel = panelState.showPanel && showEmojiPanel,
                onToggleEmojiPanel = {
                    panelState.togglePanel(it) {
                        showEmojiPanel = it
                        showPreviewPanel = false
                    }
                },
                showPreviewPanel = panelState.showPanel && showPreviewPanel,
                onTogglePreviewPanel = {
                    panelState.togglePanel(it) {
                        showEmojiPanel = false
                        showPreviewPanel = it
                    }
                }
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = LayoutPaddingHalf)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.small),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            ) {
                BmgTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    contentPadding = PaddingValues(LayoutPaddingHalf),
                    value = state.comment,
                    onValueChange = { onEvent(CommentEvent.OnTextChange(it)) },
                    shape = MaterialTheme.shapes.small,
                    autoFocus = true,
                    maxLines = 6,
                    minLines = 3,
                    placeholder = {
                        if (state.anchor.reply != ComposeComment.Empty) {
                            Text(
                                text = buildAnnotatedString {
                                    pushStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    append(state.anchor.reply.user.nickname)
                                    pop()
                                    append(": ")
                                    append(state.anchor.reply.commentHtml.text)
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = LocalTextStyle.current
                            )
                        } else {
                            Text(text = stringResource(Res.string.reply_comment_hint))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )

                Button(
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = LayoutPaddingHalf, end = LayoutPaddingHalf)
                        .resetSize(),
                    enabled = state.comment.text.isNotBlank() && !state.sending,
                    onClick = {
                        onEvent(CommentEvent.SendComment(state.comment.text))
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.alpha(if (state.sending) 0f else 1f),
                            text = if (state.anchor.reply == ComposeComment.Empty) {
                                stringResource(Res.string.reply_comment_send)
                            } else {
                                stringResource(Res.string.reply_comment)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )

                        if (state.sending) CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
private fun InputActionBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    showEmojiPanel: Boolean = false,
    onToggleEmojiPanel: (Boolean) -> Unit = {},
    showPreviewPanel: Boolean = false,
    onTogglePreviewPanel: (Boolean) -> Unit = {},
    onPickImage: () -> Unit = {},
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
            verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf / 2),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            InputActionBarButton(BgmDefaultIcons.FormatBold, size = 24.dp, bbCode = bbcodeBold) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatItalic, size = 22.dp, bbCode = bbcodeItalic) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatUnderlined, bbCode = bbcodeUnderline) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatStrikethrough, bbCode = bbcodeStrikethrough) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatSize, bbCode = bbcodeFontSize) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.VisibilityOff, bbCode = bbcodeMask) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatColorText, bbCode = bbcodeFontColor) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.AddLink, bbCode = bbcodeLink) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.Code, bbCode = bbcodeCode) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.FormatQuote, bbCode = bbcodeQuote) {
                onValueChange(value.insertBBCode(it))
            }
            InputActionBarButton(BgmDefaultIcons.Image) {
                onPickImage()
            }
            InputActionBarButton(if (showEmojiPanel) BgmIcons.Keyboard else BgmIcons.EmojiEmotions) {
                onToggleEmojiPanel(!showEmojiPanel)
            }
            InputActionBarButton(if (showPreviewPanel) BgmIcons.Keyboard else BgmIcons.Preview) {
                onTogglePreviewPanel(!showPreviewPanel)
            }
        }
    }
}

@Composable
private fun InputActionBarButton(
    icon: ImageVector,
    size: Dp = 20.dp,
    modifier: Modifier = Modifier,
    bbCode: BBCode = bbcodeEmpty,
    onClick: (BBCode) -> Unit = {},
) {
    IconButton(
        modifier = modifier.size(32.dp),
        onClick = { onClick(bbCode) }
    ) {
        Icon(
            icon,
            modifier = Modifier.size(size),
            contentDescription = null
        )
    }
}

@Composable
private fun CommentDialogPanel(
    modifier: Modifier,
    showEmojiPanel: Boolean,
    showPreviewPanel: Boolean,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
) {
    when {
        showEmojiPanel -> {
            val bgmEmojis = rememberBgmEmojis()
            val tvEmojis = rememberTvEmojis()
            val tvExtend1Emojis = rememberTvExtend1Emojis()
            val tvExtend2Emojis = rememberTvExtend2Emojis()

            BgmTabHorizontalPager(
                modifier = modifier,
                style = MaterialTheme.typography.bodyMedium,
                tabs = remember {
                    persistentListOf(
                        ComposeTextTab(0, Res.string.emoji_tv),
                        ComposeTextTab(1, Res.string.emoji_tv_1),
                        ComposeTextTab(2, Res.string.emoji_tv_2),
                        ComposeTextTab(3, Res.string.emoji_normal),
                    )
                }
            ) {
                val context = LocalPlatformContext.current

                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(40.dp),
                    contentPadding = PaddingValues(LayoutPaddingHalf),
                    horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
                ) {
                    val emojis = when (it) {
                        0 -> tvEmojis
                        1 -> tvExtend1Emojis
                        2 -> tvExtend2Emojis
                        else -> bgmEmojis
                    }
                    items(emojis) { emoji ->
                        AsyncImage(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { onValueChange(value.insertEmoji(emoji)) }
                                .padding(LayoutPaddingHalf),
                            model = remember(emoji.smileId) {
                                ImageRequest.Builder(context)
                                    .data(emoji.image.toComposeUri())
                                    .build()
                            },
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            filterQuality = FilterQuality.None
                        )
                    }
                }
            }
        }

        showPreviewPanel -> {
            BgmTabHorizontalPager(
                modifier = modifier,
                tabs = remember { persistentListOf(ComposeTextTab(0, Res.string.global_preview)) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(LayoutPadding)
                ) {
                    var previewText by remember { mutableStateOf(AnnotatedString("")) }

                    LaunchedEffect(value.text) {
                        previewText = bbcodeToHtml(value.text, true).parseAsHtml()
                    }

                    BgmLinkedText(
                        modifier = Modifier.fillMaxSize(),
                        text = previewText,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCommentDialogContent() {
    PreviewColumn {
        CommentDialogContent(
            dialogState = rememberAlertDialogState(),
            state = CommentState(
                anchor = CommentDialogAnchor(
                    article = ComposeTopicDetail.Empty,
                    reply = ComposeComment.Empty,
                    lastViewedInMillis = 0
                )
            ),
            onEvent = {

            }
        )
    }
}