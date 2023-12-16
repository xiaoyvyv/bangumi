package com.xiaoyv.bangumi.ui.feature.login

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.LoginFormEntity
import com.xiaoyv.common.api.parser.entity.LoginResultEntity
import com.xiaoyv.common.api.parser.impl.LoginParser.parserLoginForms
import com.xiaoyv.common.api.parser.impl.LoginParser.parserLoginResult
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.sendValue
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

    private var formEntity: LoginFormEntity = LoginFormEntity()

    override fun onViewCreated() {
        launchUI {
            refreshFormImpl()
            refreshVerifyImpl()
        }
    }

    fun refreshVerifyCode(loading: Boolean = false) {
        launchUI(
            state = if (loading) loadingDialogState(cancelable = false) else null,
            error = { it.printStackTrace() },
            block = {
                refreshFormImpl()
                refreshVerifyImpl()
            }
        )
    }

    private suspend fun refreshVerifyImpl() {
        onVerifyCodeLiveData.value = withContext(Dispatchers.IO) {
            val map = mapOf(System.currentTimeMillis().toString() to "")
            BgmApiManager.bgmWebApi.queryLoginVerify(map).bytes()
        }
    }

    private suspend fun refreshFormImpl() {
        formEntity = withContext(Dispatchers.IO) {
            BgmApiManager.bgmWebApi.queryLoginPage().parserLoginForms()
        }

        val resultEntity = formEntity.loginInfo
        if (formEntity.hasLogin && resultEntity != null) {
            onLoginResultLiveData.sendValue(resultEntity)

            debugLog { "当前用户已经登录成功，无需再登录" }
        }
    }

    fun doLogin(email: String, password: String, verifyCode: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onLoginResultLiveData.value = null
            },
            block = {
                val loginResult = withContext(Dispatchers.IO) {
                    val forms = formEntity.forms
                    forms["email"] = email
                    forms["password"] = password
                    forms["captcha_challenge_field"] = verifyCode

                    BgmApiManager.bgmWebApi.doLogin(param = forms).parserLoginResult()
                }

                UserHelper.refresh()
                UserHelper.cacheEmailAndPassword(email, password)

                onLoginResultLiveData.value = loginResult
            }
        )
    }
}