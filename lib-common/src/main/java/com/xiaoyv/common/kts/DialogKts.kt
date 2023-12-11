package com.xiaoyv.common.kts

import android.content.Context
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * showConfirmDialog
 *
 * @author why
 * @since 12/7/23
 */
inline fun Context.showConfirmDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    message: String = "",
    confirmText: String? = getString(CommonString.common_done),
    neutralText: String? = null,
    cancelText: String? = getString(CommonString.common_cancel),
    cancelable: Boolean = true,
    crossinline onNeutralClick: (() -> Unit) = {},
    crossinline onCancelClick: (() -> Unit) = {},
    crossinline onConfirmClick: (() -> Unit) = {},
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(cancelable)
        .let {
            if (neutralText != null) {
                it.setNeutralButton(neutralText) { _, _ ->
                    onNeutralClick()
                }
            } else {
                it
            }
        }
        .let {
            if (cancelText != null) {
                it.setNegativeButton(cancelText) { _, _ ->
                    onCancelClick()
                }
            } else {
                it
            }
        }
        .let {
            if (confirmText != null) {
                it.setPositiveButton(confirmText) { _, _ ->
                    onConfirmClick()
                }
            } else {
                it
            }
        }
        .create()
        .apply {
            setCanceledOnTouchOutside(cancelable)
        }.show()
}

inline fun Context.showOptionsDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    items: List<String>,
    crossinline onItemClick: (String, Int) -> Unit = { _, _ -> },
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setItems(items.toTypedArray()) { _, which ->
            onItemClick(items[which], which)
        }
        .create()
        .show()
}