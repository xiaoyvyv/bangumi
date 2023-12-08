package com.xiaoyv.common.widget.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.xiaoyv.common.databinding.ViewLoadingBinding
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.updateWindowParams
import com.xiaoyv.widget.kts.useNotNull
import kotlin.math.roundToInt

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
        binding.ivLogo.pauseAnimation()
        dismiss()
    }

    override fun showLoading(activity: FragmentActivity, msg: String?) {
        message = msg
        show()
        binding.ivLogo.playAnimation()
    }

    override fun onStart() {
        super.onStart()
        useNotNull(window) {
            setDimAmount(0.25f)
            setBackgroundDrawableResource(com.xiaoyv.widget.R.color.ui_transparent)
            updateWindowParams {
                width = (ScreenUtils.getScreenWidth() * 0.5).roundToInt()
            }
        }

        setCancelable(canCancelable)
        setCanceledOnTouchOutside(canCancelable)
    }
}