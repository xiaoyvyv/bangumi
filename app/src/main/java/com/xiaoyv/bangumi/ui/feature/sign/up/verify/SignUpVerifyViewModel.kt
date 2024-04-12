package com.xiaoyv.bangumi.ui.feature.sign.up.verify

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SignInResultEntity
import com.xiaoyv.common.api.parser.entity.SignUpResultEntity
import com.xiaoyv.common.api.parser.entity.SignUpVerifyEntity
import com.xiaoyv.common.api.parser.impl.SignInParser.parserLoginResult
import com.xiaoyv.common.api.parser.impl.parserSignUpVerify
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.UserTokenHelper
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignUpVerifyViewModel : BaseViewModel() {
    internal val onSignUpVerifyLiveData = MutableLiveData<SignUpVerifyEntity?>()

    internal var signUpInfo: SignUpResultEntity? = null

    fun doSignUpVerify(verifyCode: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onSignUpVerifyLiveData.value = SignUpVerifyEntity(
                    success = false,
                    error = it.errorMsg
                )
            },
            block = {
                val loginResult = withContext(Dispatchers.IO) {
                    val forms = signUpInfo?.forms ?: hashMapOf()
                    forms["token"] = verifyCode
                    BgmApiManager.bgmWebApi.doSignUpVerify(param = forms).parserSignUpVerify()
                }


                onSignUpVerifyLiveData.value = loginResult
            }
        )
    }
}