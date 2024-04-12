package com.xiaoyv.bangumi.ui.feature.sign.up

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.databinding.ActivitySignUpActionBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.showToastCompat


/**
 * Class: [SignUpActionActivity]
 *
 * @author why
 * @since 11/25/23
 */
class SignUpActionActivity : BaseViewModelActivity<ActivitySignUpActionBinding, SignUpViewModel>() {

    override fun initView() {
        enableEdgeToEdge()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
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
    }

    override fun initListener() {
        binding.btnSignUp.setOnFastLimitClickListener {
            if (!binding.checkPrivacy.isChecked) {
                showToastCompat("您必须阅读并同意《用户协议》和《隐私政策》")
                return@setOnFastLimitClickListener
            }

            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val passwordAgain = binding.inputPasswordAgain.text.toString()
            val nickname = binding.inputNickname.text.toString()
            val verifyCode = binding.inputVerify.text.toString()

            binding.tilEmail.error =
                if (email.isBlank()) getString(CommonString.login_input_error_email) else null
            binding.tilPassword.error =
                if (password.isBlank()) getString(CommonString.login_input_error_password) else null
            binding.tilPasswordAgain.error =
                if (passwordAgain.isBlank()) getString(CommonString.sign_up_input_error_password_again) else null
            binding.tilNickname.error =
                if (nickname.isBlank()) getString(CommonString.sign_up_input_error_nickname) else null
            binding.tilVerify.error =
                if (verifyCode.isBlank()) getString(CommonString.login_input_error_verify) else null

            if (email.isNotBlank() && password.isNotBlank() && verifyCode.isNotBlank()) {
                KeyboardUtils.hideSoftInput(this)
                viewModel.doSignUp(email, password, passwordAgain, nickname, verifyCode)
            }

        }

        binding.ivVerify.setOnFastLimitClickListener {
            binding.inputVerify.text = null
            viewModel.refreshVerifyCode(true)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onVerifyCodeLiveData.observe(this) {
            binding.ivVerify.loadImageAnimate(it)
        }

        viewModel.onSignUpResultLiveData.observe(this) {
            val success = it != null && it.success
            val errorMsg = it?.error.orEmpty()
            val message = it?.message.orEmpty()
            val email = binding.inputEmail.text.toString().trim()

            MaterialAlertDialogBuilder(activity)
                .setTitle(
                    if (success) getString(CommonString.login_result_tip)
                    else getString(CommonString.sign_up_error)
                )
                .setCancelable(success)
                .setMessage(if (success) message else errorMsg)
                .setPositiveButton(getString(CommonString.login_result_known)) { _, _ ->
                    if (success) {
                        RouteHelper.jumpWeb(url = BgmApiManager.buildVerifyEmailUrl(email))
                        finish()
                    } else {
                        binding.inputVerify.text = null
                        viewModel.refreshVerifyCode(false)

                        // 针对 12 小时内只能注册一个帐号的情况，直接询问是否直接重新跳转输入邮箱的激活码页面
                        if (errorMsg.contains("只能注册一个帐号") && errorMsg.contains("IP")) {
                            showRetryEnterEmailVerify()
                        }
                    }
                }
                .create()
                .apply {
                    setCanceledOnTouchOutside(success)
                }.show()
        }
    }

    /**
     * 针对 12 小时内只能注册一个帐号的情况，直接询问是否直接重新跳转输入邮箱的激活码页面
     */
    private fun showRetryEnterEmailVerify() {
        val email = binding.inputEmail.text.toString()
        if (email.isBlank()) return

        showConfirmDialog(
            title = "温馨提示",
            message = "同一个 IP 12 小时内只能注册一个帐号，若你 12 小时内已经注册过了该邮箱，是否直接去激活当前邮箱？",
            onConfirmClick = {
                // viewModel.reloadSignUpVerify(email)

                RouteHelper.jumpWeb(url = BgmApiManager.buildVerifyEmailUrl(email))
                finish()
            }
        )
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}