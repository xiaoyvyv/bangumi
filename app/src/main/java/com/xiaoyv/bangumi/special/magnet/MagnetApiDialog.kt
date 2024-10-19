package com.xiaoyv.bangumi.special.magnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.bangumi.databinding.ActivityMagnetApiBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.toast

/**
 * Class: [MagnetApiDialog]
 *
 * @author why
 * @since 1/1/24
 */
class MagnetApiDialog : BottomSheetDialogFragment() {
    private var onSaveClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ActivityMagnetApiBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivityMagnetApiBinding.bind(view)

        binding.etApi.setText(ConfigHelper.magnetSearchApi)
        binding.tvNode.setOnFastLimitClickListener {
            RouteHelper.jumpWeb(
                url = "https://github.com/kansaer/dandanplay-apiNode",
                forceBrowser = true
            )
        }

        binding.btnCancel.setOnClickListener { dismissAllowingStateLoss() }

        binding.btnDone.setOnClickListener {
            val api = binding.etApi.text.toString().trim()
            if (api.isNotBlank() && URLUtil.isNetworkUrl(api)) {
                ConfigHelper.magnetSearchApi = api.trimEnd('/')
                onSaveClick?.invoke()
                dismissAllowingStateLoss()
            } else {
                toast(i18n(CommonString.common_input_right_link))
            }
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, onSaveListener: (() -> Unit)? = null) {
            MagnetApiDialog().apply { onSaveClick = onSaveListener }
                .show(fragmentManager, "MagnetApiDialog")
        }
    }
}