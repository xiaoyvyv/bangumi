package com.xiaoyv.bangumi.ui.feature.login

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.databinding.ActivityLoginBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [LoginActivity]
 *
 * @author why
 * @since 11/25/23
 */
class LoginActivity : BaseViewModelActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun initView() {
        enableEdgeToEdge()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.inputEmail.setText(UserHelper.cacheEmail)
        binding.inputPassword.setText(UserHelper.cachePassword)
    }

    override fun initListener() {
        binding.btnLogin.setOnFastLimitClickListener {
            if (!binding.checkPrivacy.isChecked) {
                showToastCompat("您必须阅读并同意《用户协议》和《隐私政策》")
                return@setOnFastLimitClickListener
            }

            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val verifyCode = binding.inputVerify.text.toString()

            if (email.isBlank()) {
                binding.inputEmail.error = getString(CommonString.login_input_error_email)
            }
            if (password.isBlank()) {
                binding.inputPassword.error = getString(CommonString.login_input_error_password)
            }
            if (verifyCode.isBlank()) {
                binding.inputVerify.error = getString(CommonString.login_input_error_verify)
            }

            if (email.isNotBlank() && password.isNotBlank() && verifyCode.isNotBlank()) {
                KeyboardUtils.hideSoftInput(this)

                viewModel.doLogin(email, password, verifyCode)
            }
        }

        SpanUtils.with(binding.tvPrivacy)
            .append("我已阅读并同意")
            .append("《用户协议》")
            .setClickSpan(getAttrColor(GoogleAttr.colorPrimary), false) {
                RouteHelper.jumpWeb(
                    GlobalConfig.docArgument,
                    fitToolbar = true,
                    smallToolbar = true
                )
            }
            .append("和")
            .append("《隐私政策》")
            .setClickSpan(getAttrColor(GoogleAttr.colorPrimary), false) {
                RouteHelper.jumpWeb(
                    GlobalConfig.docPrivacy,
                    fitToolbar = true,
                    smallToolbar = true
                )
            }
            .create()

        binding.ivVerify.setOnFastLimitClickListener {
            binding.inputVerify.text = null
            viewModel.refreshVerifyCode(true)
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onVerifyCodeLiveData.observe(this) {
            binding.ivVerify.loadImageAnimate(it)
        }

        viewModel.onLoginResultLiveData.observe(this) {
            val loginSuccess = it?.success == true
            val errorMsg = it?.error.orEmpty()
            val message = it?.message.orEmpty()

            MaterialAlertDialogBuilder(activity)
                .setTitle(
                    if (loginSuccess) getString(CommonString.login_result_tip)
                    else getString(CommonString.login_result_error)
                )
                .setCancelable(loginSuccess)
                .setMessage(if (loginSuccess) message else errorMsg)
                .setPositiveButton(getString(CommonString.login_result_known)) { _, _ ->
                    if (loginSuccess) {
                        finish()
                    } else {
                        binding.inputVerify.text = null
                        viewModel.refreshVerifyCode(false)
                    }
                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(loginSuccess)
                }.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}