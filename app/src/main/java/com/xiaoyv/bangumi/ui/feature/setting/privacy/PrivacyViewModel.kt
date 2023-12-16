package com.xiaoyv.bangumi.ui.feature.setting.privacy

import com.blankj.utilcode.util.CloneUtils
import com.google.gson.reflect.TypeToken
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.PrivacyEntity
import com.xiaoyv.common.api.parser.impl.parserPrivacy
import com.xiaoyv.common.config.annotation.PrivacyType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [PrivacyViewModel]
 *
 * @author why
 * @since 12/17/23
 */
class PrivacyViewModel : BaseListViewModel<PrivacyEntity>() {

    override suspend fun onRequestListImpl(): List<PrivacyEntity> {
        require(UserHelper.isLogin) { "你还有没有登录呢" }
        return BgmApiManager.bgmWebApi.queryPrivacy().parserPrivacy()
    }

    /**
     * 刷新
     */
    fun changeNotifySetting(field: String, @PrivacyType type: Int) {
        val entities = onListLiveData.value.orEmpty()
        onListLiveData.value =
            CloneUtils.deepClone(entities, object : TypeToken<List<PrivacyEntity>>() {}.type)
                .onEach {
                    if (it.id == field) {
                        it.privacyType = type
                    }
                }
    }

    /**
     * 保存隐私配置
     */
    fun savePrivacy() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    require(UserHelper.isLogin) { "你还有没有登录呢" }

                    // 参数
                    val map = onListLiveData.value.orEmpty()
                        .associate { it.id to it.privacyType.toString() }
                        .toMutableMap()

                    // 添加 hash
                    map["formhash"] = UserHelper.formHash
                    map["submit_privacy"] = "保存"

                    BgmApiManager.bgmWebApi.postPrivacy(param = map)
                }

                // 刷新
                refresh()
            }
        )
    }
}