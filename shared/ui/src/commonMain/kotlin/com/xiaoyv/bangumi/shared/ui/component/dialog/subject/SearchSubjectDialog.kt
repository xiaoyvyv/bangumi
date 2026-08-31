package com.xiaoyv.bangumi.shared.ui.component.dialog.subject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.publish_add_subject_hint
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginGrid
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectLineItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

/**
 * 搜索并选择条目的半屏弹窗。
 *
 * @param state 弹窗显示状态
 * @param key 同一页面内弹窗实例的稳定标识
 * @param onSelectSubject 选中条目的回调
 */
@Composable
fun SearchSubjectDialog(
    state: BottomSheetDialogState = rememberSheetDialogState(skipPartiallyExpanded = false),
    key: String = "search-subject-dialog",
    onSelectSubject: (ComposeSubject) -> Unit,
) {
    if (state.showing) {
        val viewModel = koinViewModel<SearchSubjectDialogViewModel>(key = key)
        val uiState by viewModel.collectAsState()

        viewModel.collectBaseSideEffect {

        }

        BottomSheetDialog(state = state) {
            SearchSubjectDialogContent(
                state = uiState.data,
                onQueryChange = { viewModel.onEvent(SearchSubjectDialogEvent.OnQueryChange(it)) },
                onSelectSubject = { subject ->
                    onSelectSubject(subject)
                    state.dismiss()
                },
            )
        }
    }
}

/**
 * 条目搜索弹窗的内容。
 *
 * @param state 当前搜索状态
 * @param onQueryChange 搜索词变化回调
 * @param onSelectSubject 选中条目的回调
 */
@Composable
fun SearchSubjectDialogContent(
    state: SearchSubjectDialogState,
    onQueryChange: (TextFieldValue) -> Unit,
    onSelectSubject: (ComposeSubject) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val focusRequester = remember { FocusRequester() }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentMargin)
                .focusRequester(focusRequester),
            value = state.query,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            placeholder = { Text(stringResource(Res.string.publish_add_subject_hint)) },
            onValueChange = onQueryChange,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(state.subjects) {
                SubjectLineItem(
                    contentPadding = PaddingValues(horizontal = ContentMargin, vertical = ContentMarginGrid),
                    display = ComposeSubjectRelation(subject = it),
                    onClick = {
                        onQueryChange(TextFieldValue())
                        onSelectSubject(it)
                    },
                )
                HorizontalDivider()
            }
        }

        LaunchedEffect(Unit) {
            if (state.query.text.isBlank()) focusRequester.requestFocus()
        }
    }
}
