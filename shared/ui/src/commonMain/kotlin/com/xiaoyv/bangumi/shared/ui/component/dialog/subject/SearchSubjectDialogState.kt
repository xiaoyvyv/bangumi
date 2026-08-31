package com.xiaoyv.bangumi.shared.ui.component.dialog.subject

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentListOf

/**
 * 条目搜索弹窗的状态。
 *
 * @param query 当前输入的搜索关键词
 * @param subjects 搜索到的条目列表
 */
@Immutable
data class SearchSubjectDialogState(
    val query: TextFieldValue = TextFieldValue(),
    val subjects: SerializeList<ComposeSubject> = persistentListOf(),
)
