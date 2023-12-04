package com.xiaoyv.bangumi.ui.feature.user

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.UserDetailEntity
import com.xiaoyv.common.api.parser.impl.parserUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [UserViewModel]
 *
 * @author why
 * @since 12/3/23
 */
class UserViewModel : BaseViewModel() {
    internal val onUserInfoLiveData = MutableLiveData<UserDetailEntity?>()

    var userId: String = ""

    override fun onViewCreated() {
        queryUserInfo()
    }

    private fun queryUserInfo() {
        launchUI(
            error = {
                it.printStackTrace()
                onUserInfoLiveData.value = null
            },
            block = {
                onUserInfoLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryUserInfo(userId).parserUserInfo(userId)
                }
            }
        )
    }
}