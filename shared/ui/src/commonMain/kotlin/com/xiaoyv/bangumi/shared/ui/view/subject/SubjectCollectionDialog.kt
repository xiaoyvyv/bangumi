package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_clear
import com.xiaoyv.bangumi.core_resource.resources.global_private
import com.xiaoyv.bangumi.core_resource.resources.global_public
import com.xiaoyv.bangumi.core_resource.resources.global_update
import com.xiaoyv.bangumi.core_resource.resources.subject_comment_hint
import com.xiaoyv.bangumi.core_resource.resources.subject_tag_hint
import com.xiaoyv.bangumi.core_resource.resources.subject_tag_mine
import com.xiaoyv.bangumi.core_resource.resources.subject_tag_normal
import com.xiaoyv.bangumi.core_resource.resources.type_score_unset
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.types.ScoreStarType
import com.xiaoyv.bangumi.shared.core.utils.appendText
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.PreviewComposeSubject
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.ui.component.bar.RatingSeekBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.text.BmgTextField
import com.xiaoyv.bangumi.shared.ui.component.text.textFieldTransparentColors
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource

/**
 * 更新条目收藏弹窗
 */
@Composable
fun SubjectCollectionDialog(
    state: BottomSheetDialogState,
    subject: ComposeSubject,
    myTags: SerializeList<ComposeTag> = persistentListOf(),
    loadState: LoadingState = LoadingState.NotLoading,
    onCollectionUpdate: (CollectionSubjectUpdate) -> Unit,
) {
    BottomSheetDialog(
        modifier = Modifier.fillMaxWidth(),
        state = state,
        contentWindowInsets = { WindowInsets() },
    ) {

        SubjectCollectionDialogContent(
            subject = subject,
            myTags = myTags,
            loadState = loadState,
            onCollectionUpdate = onCollectionUpdate
        )
    }

    LaunchedEffect(loadState) {
        if (loadState == LoadingState.NotLoading) {
            state.dismissNow()
        }
    }
}

@Composable
fun SubjectCollectionDialogContent(
    subject: ComposeSubject,
    myTags: SerializeList<ComposeTag>,
    loadState: LoadingState,
    onCollectionUpdate: (CollectionSubjectUpdate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Bottom))
            .padding(LayoutPadding),
        verticalArrangement = Arrangement.spacedBy(LayoutPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = subject.displayName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
        )

        if (subject.nameCn.isNotEmpty()) Text(
            text = subject.name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        val value = remember { subject.interest.toCollectionSubjectUpdate() }
        var tagPanelIndex by remember { mutableStateOf(0) }
        var tagInput by remember { mutableStateOf(value.tags.orEmpty().joinToString(" ").asTextFieldValue()) }
        var commentInput by remember { mutableStateOf(value.comment.orEmpty().asTextFieldValue()) }
        var rate by remember { mutableStateOf(value.rate ?: 0) }
        var type by remember { mutableStateOf(value.type ?: CollectionType.UNKNOWN) }
        var private by remember { mutableStateOf(value.private ?: false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LayoutPadding),
        ) {
            // 评分组件
            if (type != CollectionType.WISH && type != CollectionType.UNKNOWN) {
                Text(
                    modifier = Modifier.clickWithoutRipped { rate = ScoreStarType.TYPE_0 },
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(if (rate == 0) stringResource(Res.string.type_score_unset) else ScoreStarType.string(rate))
                        }
                        append(" / ")
                        append(stringResource(Res.string.global_clear))
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                RatingSeekBar(
                    rating = rate,
                    stars = 5,
                    starSize = 40.dp,
                    onRatingChanged = { rate = it }
                )
            }
        }

        BmgTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
            value = tagInput,
            singleLine = true,
            onValueChange = { tagInput = it },
            colors = textFieldTransparentColors(),
            placeholder = {
                Text(text = stringResource(Res.string.subject_tag_hint))
            }
        )

        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .clickWithoutRipped { tagPanelIndex = if (tagPanelIndex == 0) 1 else 0 },
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = if (tagPanelIndex == 0) MaterialTheme.colorScheme.primary else Color.Unspecified,
                        fontWeight = if (tagPanelIndex == 0) FontWeight.Medium else FontWeight.Normal
                    )
                ) {
                    append(stringResource(Res.string.subject_tag_normal))
                }
                append(" / ")
                withStyle(
                    SpanStyle(
                        color = if (tagPanelIndex == 1) MaterialTheme.colorScheme.primary else Color.Unspecified,
                        fontWeight = if (tagPanelIndex == 1) FontWeight.Medium else FontWeight.Normal
                    )
                ) {
                    append(stringResource(Res.string.subject_tag_mine))
                }
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        SubjectTagRow(
            modifier = Modifier.fillMaxWidth(),
            tags = if (tagPanelIndex == 0) subject.tags else myTags,
            onClick = {
                if (!tagInput.text.contains(it.name)) {
                    tagInput = tagInput.appendText(" " + it.name)
                }
            }
        )

        BmgTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
            value = commentInput,
            maxLines = 4,
            minLines = 4,
            onValueChange = { commentInput = it },
            colors = textFieldTransparentColors(),
            placeholder = {
                Text(text = stringResource(Res.string.subject_comment_hint))
            }
        )

        val options = listOf(
            CollectionType.WISH to BgmIcons.FavoriteBorder,
            CollectionType.DONE to BgmIcons.Done,
            CollectionType.DOING to BgmIcons.Visibility,
            CollectionType.ASIDE to BgmIconsMirrored.Undo,
            CollectionType.DROP to BgmIcons.DeleteSweep,
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (t, _) ->
                SegmentedButton(
                    selected = type == t,
                    onClick = { type = t },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size, baseShape = MaterialTheme.shapes.small),
                    label = {
                        Text(text = CollectionType.string(subject.type, t))
                    }
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = LayoutPadding),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = { private = !private }
            ) {
                if (private) {
                    Text(text = stringResource(Res.string.global_private))
                } else {
                    Text(text = stringResource(Res.string.global_public))
                }
            }
            Button(
                modifier = Modifier.weight(2f),
                shape = MaterialTheme.shapes.small,
                enabled = loadState != LoadingState.Loading && type != CollectionType.UNKNOWN,
                onClick = {
                    onCollectionUpdate(
                        CollectionSubjectUpdate(
                            type = type,
                            rate = if (rate == 0) null else rate,
                            comment = commentInput.text.takeIf { it.isNotBlank() },
                            private = private,
                            tags = tagInput.text.split(" ")
                                .mapNotNull { it.trim().takeIf { tag -> tag.isNotBlank() } }
                                .take(10)
                                .takeIf { it.isNotEmpty() }?.toPersistentList(),
                        )
                    )
                }
            ) {
                if (loadState == LoadingState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = stringResource(Res.string.global_update))

                    if (loadState is LoadingState.Error) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            modifier = Modifier.weight(1f, false),
                            text = loadState.error.errMsg,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Preview(widthDp = 320, heightDp = 842)
@Composable
fun SubjectCollectionDialogPreview() {
    PreviewColumn {
        SubjectCollectionDialogContent(
            PreviewComposeSubject,
            myTags = persistentListOf(),
            loadState = LoadingState.NotLoading,
            onCollectionUpdate = {

            },
        )
    }
}