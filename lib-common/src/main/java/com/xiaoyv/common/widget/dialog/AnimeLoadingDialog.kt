package com.xiaoyv.common.widget.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewLoadingBinding
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.updateWindowParams

/**
 * AnimeLoadingDialog
 *
 * @author why
 * @since 11/19/23
 */
class AnimeLoadingDialog(context: Context) : AlertDialog(context), UiDialog {
    override var canCancelable: Boolean = false

    override var message: String? = ""

    private val binding = ViewLoadingBinding.inflate(LayoutInflater.from(context))

    init {
        setView(binding.root)
    }

    override fun addOnDismissListener(dismissListener: UiDialog.OnDismissListener) {

    }

    override fun addOnShowListener(showListener: UiDialog.OnShowListener) {

    }

    override fun dismissLoading() {
        dismiss()
    }

    override fun showLoading(activity: FragmentActivity, msg: String?) {
        message = msg
        show()
    }

    override fun onStart() {
        super.onStart()

        val window = window ?: return
        window.setDimAmount(ConfigHelper.DIALOG_DIM_AMOUNT)
        window.setBackgroundDrawableResource(com.xiaoyv.widget.R.color.ui_transparent)
        window.updateWindowParams {
            width = 24.dpi * 2 + context.resources.getDimensionPixelSize(R.dimen.avatar_size)
        }

        setCancelable(canCancelable)
        setCanceledOnTouchOutside(canCancelable)
    }
}