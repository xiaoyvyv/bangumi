package com.xiaoyv.bangumi.special.thunder

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.bangumi.databinding.FragmentThunderDialogBinding
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [ThunderDialog]
 *
 * @author why
 * @since 3/24/24
 */
class ThunderDialog : BottomSheetDialogFragment() {
    var onInputUrlListener: (Uri) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentThunderDialogBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentThunderDialogBinding.bind(view)

        binding.secTitle.title = StringUtils.getString(CommonString.download_dialog_title)
        binding.secTitle.more = null
        binding.btnDone.setOnFastLimitClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isNotBlank()) {
                onInputUrlListener.invoke(Uri.parse(url))
                dismissAllowingStateLoss()
            }
        }

        binding.etUrl.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        KeyboardUtils.showSoftInput()

        // 初始 peek 高度
        val dialog = dialog as? BottomSheetDialog ?: return
        dialog.behavior.peekHeight = ScreenUtils.getScreenHeight()
    }

    companion object {
        fun show(fragmentActivity: FragmentActivity, onInputUrl: (Uri) -> Unit = {}) {
            val dialog = ThunderDialog()
            dialog.onInputUrlListener = onInputUrl
            dialog.show(
                fragmentActivity.supportFragmentManager,
                ThunderDialog::class.simpleName
            )
        }
    }
}