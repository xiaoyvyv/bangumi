package com.xiaoyv.bangumi.ui.feature.login

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.KeyboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityLoginBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [LoginActivity]
 *
 * @author why
 * @since 11/25/23
 */
class LoginActivity : BaseViewModelActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun initView() {
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.btnLogin.setOnFastLimitClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val verifyCode = binding.inputVerify.text.toString()

            if (email.isBlank()) {
                binding.inputEmail.error = getString(R.string.login_input_error_email)
            }
            if (password.isBlank()) {
                binding.inputPassword.error = getString(R.string.login_input_error_password)
            }
            if (verifyCode.isBlank()) {
                binding.inputVerify.error = getString(R.string.login_input_error_verify)
            }

            if (email.isNotBlank() && password.isNotBlank() && verifyCode.isNotBlank()) {
                KeyboardUtils.hideSoftInput(this)

                viewModel.doLogin(email, password, verifyCode)
            }
        }

        binding.ivVerify.setOnFastLimitClickListener {
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
                .setTitle(if (loginSuccess) getString(R.string.login_result_tip) else getString(R.string.login_result_error))
                .setCancelable(loginSuccess)
                .setMessage(if (loginSuccess) message else errorMsg)
                .setPositiveButton(getString(R.string.login_result_known)) { _, _ ->
                    viewModel.refreshVerifyCode()
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