package com.xiaoyv.bangumi.features.search.result.page

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_done
import com.xiaoyv.bangumi.core_resource.resources.global_nsfw
import com.xiaoyv.bangumi.core_resource.resources.global_sort
import com.xiaoyv.bangumi.core_resource.resources.global_type
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.resetSize
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuChip
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.filterNsfw
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.subjectTypeTabs
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectAdvanceFilterTextField
import com.xiaoyv.bangumi.shared.ui.view.subject.splitValue
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource


/**
 * 搜索结果：条目
 */
@Composable
fun SearchResultSubject(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    val param = state.subjectParam

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val advanceDialogState = rememberSheetDialogState()

            SearchResultSubjectAdvanceFilter(
                state = state,
                dialogState = advanceDialogState,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )

            AssistChip(
                onClick = { advanceDialogState.show() },
                label = { Text("高级过滤") }
            )

            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_type),
                current = param.search.filter.type?.firstOrNull(),
                options = subjectTypeTabs,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchSubjectParam(
                            body = param.search.copy(
                                filter = param.search.filter.copy(
                                    type = persistentListOf(it.type)
                                )
                            )
                        )
                    )
                }
            )

            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_sort),
                current = param.search.sort,
                options = state.filterSubjectSort,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchSubjectParam(
                            body = param.search.copy(sort = it.type)
                        )
                    )
                }
            )

            DropMenuChip(
                labelPrefix = stringResource(Res.string.global_nsfw),
                current = param.search.filter.nsfw,
                options = filterNsfw,
                onOptionClick = {
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchSubjectParam(
                            body = param.search.copy(filter = param.search.filter.copy(nsfw = it.type))
                        )
                    )
                }
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SubjectPageRoute(
                param = param,
                onNavScreen = { onUiEvent(SearchResultEvent.UI.OnNavScreen(it)) }
            )
        }
    }
}


@Composable
fun SearchResultSubjectAdvanceFilter(
    state: SearchResultState,
    dialogState: BottomSheetDialogState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    BottomSheetDialog(state = dialogState) {
        val filter = state.subjectParam.search.filter

        val airdateState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(filter.date.orEmpty().joinToString(" ").asTextFieldValue())
        }
        val publicTagsState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(filter.metaTags.orEmpty().joinToString(" ").asTextFieldValue())
        }
        val tagsState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(filter.tags.orEmpty().joinToString(" ").asTextFieldValue())
        }
        val rankState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(filter.rank.orEmpty().joinToString(" ").asTextFieldValue())
        }
        val scoreState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(filter.rating.orEmpty().joinToString(" ").asTextFieldValue())
        }

        val fieldStates = listOf(
            airdateState,
            publicTagsState,
            tagsState,
            rankState,
            scoreState
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = LayoutPadding)
                .padding(bottom = LayoutPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "高级过滤",
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                modifier = Modifier.resetSize(),
                onClick = {
                    dialogState.dismiss()
                    onActionEvent(
                        SearchResultEvent.Action.OnUpdateSearchSubjectParam(
                            body = state.subjectParam.search.copy(
                                filter = filter.copy(
                                    date = airdateState.value.text.splitValue(),
                                    metaTags = publicTagsState.value.text.splitValue(),
                                    tags = tagsState.value.text.splitValue(),
                                    rank = rankState.value.text.splitValue(),
                                    rating = scoreState.value.text.splitValue(),
                                )
                            )
                        )
                    )
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(text = stringResource(Res.string.global_done))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val scrollState = rememberScrollState()
            val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
            var focusedIndex by remember { mutableStateOf(-1) }

            // 靠近底部输入框，弹起键盘自动滑动
            LaunchedEffect(imeHeight, focusedIndex) {
                if (imeHeight > 0 && focusedIndex >= 2) {
                    delay(50)
                    scrollState.animateScrollTo(scrollState.maxValue, tween(500))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = LayoutPadding)
                    .padding(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                SubjectAdvanceFilterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fieldStates[0].value,
                    onValueChange = { fieldStates[0].value = it },
                    title = "播出日期/发售日期",
                    hint = "YYYY-MM-DD，多个空格分隔",
                    description = "日期必需为 YYYY-MM-DD 格式，多值之间为 且 关系，多个使用空格分隔。\n示例：>=2020-07-01 <2020-10-01",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    onFocusChanged = { if (it) focusedIndex = 0 }
                )

                SubjectAdvanceFilterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fieldStates[1].value,
                    onValueChange = { fieldStates[1].value = it },
                    title = "公共标签",
                    hint = "公共标签，多个空格分隔",
                    description = "公共标签，多个值之间为 且 关系，多个使用空格分隔。可以用 - 排除标签。\n示例：日本 原创 -科幻",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    onFocusChanged = { if (it) focusedIndex = 1 }
                )

                SubjectAdvanceFilterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fieldStates[2].value,
                    onValueChange = { fieldStates[2].value = it },
                    title = "标签",
                    hint = "标签，多个空格分隔",
                    description = "标签，可以多次出现。多值之间为 且 关系，多个使用空格分隔。\n示例：日漫 日常",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    onFocusChanged = { if (it) focusedIndex = 2 }
                )

                SubjectAdvanceFilterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fieldStates[3].value,
                    onValueChange = { fieldStates[3].value = it },
                    title = "排名",
                    hint = "排名，多个空格分隔",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    description = "用于搜索指定排名的条目，多值之间为 且 关系，多个使用空格分隔。\n示例：>100 <=200",
                    onFocusChanged = { if (it) focusedIndex = 3 }
                )

                SubjectAdvanceFilterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = fieldStates[4].value,
                    onValueChange = { fieldStates[4].value = it },
                    title = "评分",
                    hint = "评分，多个空格分隔",
                    description = "用于搜索指定评分的条目，多值之间为 且 关系，多个使用空格分隔。\n示例：>6 <=10",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    onFocusChanged = { if (it) focusedIndex = 4 }
                )
            }

            // 底部运算符栏
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceBright)
                    .padding(horizontal = LayoutPadding)
                    .imePadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val operators = remember { listOf(">", ">=", "=", "<", "<=", "-") }

                operators.forEach { op ->
                    Text(
                        modifier = Modifier
                            .widthIn(min = 40.dp)
                            .clickable {
                                if (focusedIndex != -1) {
                                    val oldValue = fieldStates[focusedIndex].value
                                    val selection = oldValue.selection
                                    val newText = buildString {
                                        append(oldValue.text.substring(0, selection.start))
                                        append(op)
                                        append(oldValue.text.substring(selection.end))
                                    }
                                    val newSelection = selection.start + op.length
                                    fieldStates[focusedIndex].value = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newSelection)
                                    )
                                }
                            }
                            .padding(LayoutPaddingHalf),
                        text = op,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
