package com.xiaoyv.common.helper

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.SPUtils
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.LoginResultEntity
import com.xiaoyv.common.api.parser.impl.LoginParser.parserCheckIsLogin
import com.xiaoyv.common.api.response.UserEntity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [UserHelper]
 *
 * @author why
 * @since 11/26/23
 */
class UserHelper private constructor() {
    private val onUserInfoLiveData = MutableLiveData<UserEntity>()
    private val empty = UserEntity(isEmpty = true)

    /**
     * 加载缓存的用户
     */
    private fun initCache() {
        // 读取缓存用户
        val userEntity = userSp.getString(KEY_USER_INFO).orEmpty().fromJson<UserEntity>() ?: empty
        onUserInfoLiveData.sendValue(userEntity)

        // 有登录历史校验缓存
        launchProcess {
            withContext(Dispatchers.IO) {
                // 无登录历史跳过校验
                if (userEntity.isEmpty || userEntity.email.isNullOrBlank() || userEntity.password.isNullOrBlank()) {
                    clearUserInfo()

                    debugLog { "校验缓存用户：无登录历史" }
                    return@withContext
                }

                debugLog { "校验缓存用户：${userEntity.toJson(true)}" }

                val isLogin = BgmApiManager.bgmWebApi.queryLoginPage().parserCheckIsLogin()
                if (isLogin.not()) {
                    debugLog { "校验缓存用户：过期清理！" }

                    // 尝试自动登录
                    clearUserInfo()
                } else {
                    debugLog { "校验缓存用户：有效！" }
                }
            }
        }
    }

    private fun onLogin(loginResult: LoginResultEntity) {
        val userEntity = loginResult.userEntity
        if (loginResult.success && userEntity.isEmpty.not()) {
            onUserInfoLiveData.sendValue(userEntity)
            userSp.put(KEY_USER_INFO, userEntity.toJson())
        } else {
            onUserInfoLiveData.sendValue(empty)
            userSp.remove(KEY_USER_INFO)
        }
    }

    /**
     * 退出登录
     */
    private fun clearUserInfo() {
        BgmApiManager.resetCookie()

        userSp.clear(true)
        onUserInfoLiveData.sendValue(empty)
    }

    companion object {
        private const val KEY_USER_INFO = "user-info"

        private val helper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            UserHelper()
        }

        private val userSp: SPUtils
            get() = SPUtils.getInstance("user")

        /**
         * 当前用户
         */
        val currentUser: UserEntity
            get() = helper.onUserInfoLiveData.value ?: helper.empty

        /**
         * 是否登录
         */
        val isLogin: Boolean
            get() = !currentUser.isEmpty

        fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<UserEntity>) {
            helper.onUserInfoLiveData.observe(lifecycleOwner, observer)
        }

        fun initLoad() {
            helper.initCache()
        }

        fun onLogin(loginResult: LoginResultEntity) {
            helper.onLogin(loginResult)
        }

        fun logout() {
            launchProcess {
                withContext(Dispatchers.IO) {
                    helper.clearUserInfo()
                }
            }
        }
    }
}