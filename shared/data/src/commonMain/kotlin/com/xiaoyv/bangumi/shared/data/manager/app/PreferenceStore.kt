package com.xiaoyv.bangumi.shared.data.manager.app

import com.multiplatform.webview.cookie.WebViewCookieManager
import com.xiaoyv.bangumi.shared.data.constant.SpKey
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import com.xiaoyv.bangumi.shared.data.repository.boolean
import com.xiaoyv.bangumi.shared.data.repository.long
import com.xiaoyv.bangumi.shared.data.repository.serializable
import com.xiaoyv.bangumi.shared.data.repository.string

/**
 * 本地SP数据缓存，禁止UI模块直接调用，需要在 common层委托读写
 */
class PreferenceStore(
    cacheRepository: CacheRepository,
    private val databaseRepository: DatabaseRepository,
) {
    var userInfo: ComposeUser
        get() = databaseRepository.fetchCurrentUser()
        set(value) = databaseRepository.sendSaveUser(value)


    /**
     * 用户是否同意隐私政策
     */
    internal var isAgreePrivacy by cacheRepository.boolean(SpKey.KEY_USER_AGREE_PRIVACY)

    /**
     * 用户是首次使用
     */
    internal var isUserFirstUse by cacheRepository.boolean(SpKey.KEY_USER_FIRST_USE, default = true)

    /**
     * 用户登录的 Bangumi-Token 数据
     */
    internal var userToken by cacheRepository.serializable(SpKey.KEY_USER_BGM_TOKEN, ComposeAuthToken.Empty)

    /**
     * 用户登录的 Pixiv-Token 数据
     */
    internal var pixivToken by cacheRepository.serializable(SpKey.KEY_USER_PIXIV_TOKEN, ComposePixivToken.Empty)

    val pixivTokenData: ComposePixivToken
        get() = pixivToken

    suspend fun clearPixivToken() {
        val cookieManager = WebViewCookieManager()
        cookieManager.removeCookies(WebConstant.URL_BASE_PIXIV)
        pixivToken = ComposePixivToken.Empty
    }

    /**
     * APP 设置
     */
    internal var settings by cacheRepository.serializable(SpKey.KEY_SETTING, ComposeSetting.Default)

    /**
     * 上次启动 APP 的日期（格式：yyyy-MM-dd）
     */
    internal var lastLaunchDate by cacheRepository.string(SpKey.KEY_LAST_LAUNCH_DATE, default = "")

    /**
     * 上次启动时检测 Bangumi 服务连通性的时间戳
     */
    internal var lastBgmHostCheckTime by cacheRepository.long(SpKey.KEY_LAST_BGM_HOST_CHECK_TIME)
}
