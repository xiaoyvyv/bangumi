package com.xiaoyv.bangumi.ui.profile.edit

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.api.parser.impl.parserSettingInfo
import com.xiaoyv.common.config.annotation.EditProfileOptionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [EditProfileViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class EditProfileViewModel : BaseViewModel() {
    internal val onEditOptionLiveData = MutableLiveData<List<SettingBaseEntity>?>()

    /**
     * 提交时需要带上的隐藏表单参数
     */
    private var hiddenOptions: List<SettingBaseEntity> = emptyList()

    override fun onViewCreated() {
        queryUserInfo()
    }

    fun queryUserInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onEditOptionLiveData.value = null
            },
            block = {
                onEditOptionLiveData.value = withContext(Dispatchers.IO) {
                    val entities = BgmApiManager.bgmWebApi.querySettings()
                        .parserSettingInfo()

                    val editOptions = entities.filter {
                        it.type != "submit" && it.type != "hidden"
                    }

                    hiddenOptions = entities.filter {
                        it.type == "submit" || it.type == "hidden"
                    }

                    editOptions.sortedBy {
                        if (it.type == "file") 0
                        else 1
                    }
                }
            }
        )
    }

    fun updateSettings() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onEditOptionLiveData.value = null
            },
            block = {
                onEditOptionLiveData.value = withContext(Dispatchers.IO) {
                    val avatarFile = null
                    val userInfoMap = hashMapOf<String, String>()

                    val entities = BgmApiManager.bgmWebApi.updateSettings(
                        map = userInfoMap,
                        file = avatarFile
                    ).parserSettingInfo()

                    val editOptions = entities.filter {
                        it.type != "submit" && it.type != "hidden"
                    }

                    hiddenOptions = entities.filter {
                        it.type == "submit" || it.type == "hidden"
                    }

                    editOptions.sortedBy {
                        if (it.type == "file") 0
                        else 1
                    }
                }
            }
        )
    }

    private fun buildEditOptionItems(): List<SettingBaseEntity> {
        return listOf(
            SettingBaseEntity(
                title = "头像",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_FILE
            ),
            SettingBaseEntity(
                title = "昵称",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "签名",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "时区",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_SELECTOR
            ),
            SettingBaseEntity(
                title = "主页",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "介绍",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "PSN",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "Xbox Live",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "NS",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "FriendCode",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "Steam",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "BattleTag",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "Pixiv",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "GitHub",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "Twitter",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            ),
            SettingBaseEntity(
                title = "Instagram",
                field = "name",
                value = "",
                type = EditProfileOptionType.TYPE_INPUT
            )
        )
    }
}