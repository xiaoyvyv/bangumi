package com.xiaoyv.bangumi.ui.feature.login

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.LoginParser.parseLoginResult
import com.xiaoyv.common.api.parser.impl.LoginParser.parserLoginForms
import com.xiaoyv.common.api.parser.entity.LoginResultEntity
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [LoginViewModel]
 *
 * @author why
 * @since 11/25/23
 */
class LoginViewModel : BaseViewModel() {

    internal val onVerifyCodeLiveData = MutableLiveData<ByteArray?>()

    internal val onLoginResultLiveData = MutableLiveData<LoginResultEntity?>()

    override fun onViewCreated() {
        refreshVerifyCode()
    }

    fun refreshVerifyCode(loading: Boolean = false) {
        launchUI(
            state = if (loading) loadingDialogState(cancelable = false) else null,
            error = { it.printStackTrace() },
            block = {
                onVerifyCodeLiveData.value = withContext(Dispatchers.IO) {
                    val map = mapOf(System.currentTimeMillis().toString() to "")
                    BgmApiManager.bgmWebApi.queryLoginVerify(map).bytes()
                }
            }
        )
    }

    fun doLogin(email: String, password: String, verifyCode: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onLoginResultLiveData.value = null
            },
            block = {
                onLoginResultLiveData.value = withContext(Dispatchers.IO) {
                    val formEntity = BgmApiManager.bgmWebApi.queryLoginPage().parserLoginForms()
                    if (formEntity.hasLogin) {
                        debugLog { "当前用户已经登录成功，无需再登录" }
                        return@withContext formEntity.loginInfo
                    }

                    val forms = formEntity.forms
                    forms["email"] = email
                    forms["password"] = password
                    forms["captcha_challenge_field"] = verifyCode

                    BgmApiManager.bgmWebApi.doLogin(forms).parseLoginResult().apply {
                        debugLog { toJson(true) }
                    }
                }
            }
        )
    }
}