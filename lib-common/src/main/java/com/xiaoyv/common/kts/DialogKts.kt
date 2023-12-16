package com.xiaoyv.common.kts

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.common.widget.text.AnimeEditTextView
import com.xiaoyv.widget.kts.dpi

/**
 * showConfirmDialog
 *
 * @author why
 * @since 12/7/23
 */
inline fun Context.showConfirmDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    message: CharSequence = "",
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

/**
 * 输入弹窗
 */
inline fun Context.showInputDialog(
    title: String = StringUtils.getString(CommonString.common_tip),
    inputHint: String = "输入内容...",
    crossinline onInput: (String) -> Unit = { _ -> },
) {
    val editTextView = AnimeEditTextView(this).apply {
        updatePadding(top = 12.dpi, bottom = 12.dpi)
        layoutParams = MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        hint = inputHint
    }
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(editTextView)
        .setNegativeButton(getString(CommonString.common_cancel), null)
        .setPositiveButton(getString(CommonString.common_done)) { _, _ ->
            val content = editTextView.text.toString().trim()
            if (content.isNotBlank()) {
                onInput(editTextView.text.toString().trim())
            }
        }
        .create()
        .show()

    // 更新边距
    editTextView.updateLayoutParams<MarginLayoutParams> {
        leftMargin = 22.dpi
        rightMargin = 22.dpi
        topMargin = 16.dpi
    }

    editTextView.postDelayed({
        KeyboardUtils.showSoftInput(editTextView)
    }, 100)
}