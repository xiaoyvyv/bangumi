package com.xiaoyv.bangumi.ui.feature.user

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.UserDetailEntity
import com.xiaoyv.common.api.parser.impl.parserUserInfo
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
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
    internal val onActionResult = MutableLiveData<Boolean>()

    var userId: String = ""

    /**
     * 部分操作需要 Int 类型的 UID
     */
    internal val requireUserNumberId
        get() = onUserInfoLiveData.value?.numberUid.orEmpty().ifBlank { userId }

    internal val requireIsFriend
        get() = onUserInfoLiveData.value?.isFriend == true

    internal val requireUserName
        get() = onUserInfoLiveData.value?.nickname.orEmpty()

    private val requireGh
        get() = onUserInfoLiveData.value?.gh.orEmpty()

    override fun onViewCreated() {
        queryUserInfo()
    }

    fun queryUserInfo() {
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

    /**
     * 删除好友
     */
    fun actionFriend(addFriend: Boolean) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    val referer = BgmApiManager.buildReferer(BgmPathType.TYPE_FRIEND, userId)
                    if (addFriend) {
                        BgmApiManager.bgmWebApi.connectFriend(referer, requireUserNumberId, requireGh)
                    } else {
                        BgmApiManager.bgmWebApi.disconnectFriend(referer, requireUserNumberId, requireGh)
                    }
                }
                onActionResult.value = true
                UserHelper.notifyActionChange(BgmPathType.TYPE_USER)
            }
        )
    }

    /**
     * 屏蔽用户
     */
    fun blockUser() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    val referer = BgmApiManager.buildReferer(BgmPathType.TYPE_FRIEND, userId)
                    BgmApiManager.bgmWebApi.postIgnoreUser(referer, userId, requireGh)
                }
                onActionResult.value = true
                UserHelper.notifyActionChange(BgmPathType.TYPE_USER)
            }
        )
    }
}