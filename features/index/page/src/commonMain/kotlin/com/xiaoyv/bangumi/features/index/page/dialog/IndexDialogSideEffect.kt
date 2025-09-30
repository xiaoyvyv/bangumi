package com.xiaoyv.bangumi.features.index.page.dialog

/**
 * [IndexDialogSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class IndexDialogSideEffect {
    data object OnSaveSuccess : IndexDialogSideEffect()
}