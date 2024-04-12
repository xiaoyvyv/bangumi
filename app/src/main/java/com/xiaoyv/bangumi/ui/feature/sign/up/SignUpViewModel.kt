package com.xiaoyv.bangumi.ui.feature.sign.up

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SignUpResultEntity
import com.xiaoyv.common.api.parser.impl.parserSignUpResult
import com.xiaoyv.common.api.parser.parserFormHash
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.randId
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SignUpViewModel]
 *
 * @author why
 * @since 11/25/23
 */
class SignUpViewModel : BaseViewModel() {
    internal val onVerifyCodeLiveData = MutableLiveData<ByteArray?>()
    internal val onSignUpResultLiveData = MutableLiveData<SignUpResultEntity?>()

    private var formHash = ""

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
            val map = mapOf(randId() to "")
            BgmApiManager.bgmWebApi.queryLoginVerify(map).bytes()
        }
    }

    private suspend fun refreshFormImpl() {
        formHash = withContext(Dispatchers.IO) {
            BgmApiManager.bgmWebApi.querySignUpPage().parserFormHash()
        }
    }

    fun doSignUp(
        email: String,
        password: String,
        passwordAgain: String,
        nickname: String,
        verifyCode: String
    ) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onSignUpResultLiveData.value = SignUpResultEntity(
                    success = false,
                    error = it.errorMsg
                )
            },
            block = {
                val signUpResult = withContext(Dispatchers.IO) {

                    val forms = hashMapOf<String, String>()
                    forms["formhash"] = formHash
                    forms["referer"] = ""
                    forms["email"] = email
                    forms["password"] = password
                    forms["password2"] = passwordAgain
                    forms["nickname"] = nickname
                    forms["captcha_challenge_field"] = verifyCode
                    forms["guideline"] = "不提供"
                    forms["regsubmit"] = "注册会员"

                    BgmApiManager.bgmWebApi.doSignUp(param = forms).parserSignUpResult()
                }

                // 缓存用户信息
                if (signUpResult.success) {
                    UserHelper.cacheEmailAndPassword(email, password)
                }

                onSignUpResultLiveData.value = signUpResult
            }
        )
    }

    fun reloadSignUpVerify(email: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onSignUpResultLiveData.value = SignUpResultEntity(
                    success = false,
                    error = it.errorMsg
                )
            },
            block = {
                val signUpResult = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryLoginVerify(email = email).parserSignUpResult()
                }

                onSignUpResultLiveData.value = signUpResult
            }
        )
    }
}