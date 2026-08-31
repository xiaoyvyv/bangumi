package com.xiaoyv.bangumi.shared.ui.component.dialog.subject

import androidx.compose.ui.text.input.TextFieldValue

/**
 * 条目搜索弹窗事件。
 */
sealed class SearchSubjectDialogEvent {
    /**
     * 更新搜索关键词。
     *
     * @param query 新的关键词
     */
    data class OnQueryChange(val query: TextFieldValue) : SearchSubjectDialogEvent()
}
