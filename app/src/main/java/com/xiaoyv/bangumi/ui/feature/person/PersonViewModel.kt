package com.xiaoyv.bangumi.ui.feature.person

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.impl.parserPerson
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [PersonViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonViewModel : BaseViewModel() {
    internal var personId = ""
    internal var isVirtual = false
    internal val onPersonLiveData = MutableLiveData<PersonEntity?>()
    internal val vpEnableLiveData = MutableLiveData<Boolean>()

    /**
     * 是否收集
     */
    internal val isCollected: Boolean
        get() = onPersonLiveData.value?.isCollected == true

    override fun onViewCreated() {
        queryPersonInfo()
    }

    private fun queryPersonInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                onPersonLiveData.value = null
                it.printStackTrace()
            },
            block = {
                onPersonLiveData.value = withContext(Dispatchers.IO) {
                    if (isVirtual) {
                        BgmApiManager.bgmWebApi.queryCharacterInfo(personId)
                    } else {
                        BgmApiManager.bgmWebApi.queryPersonInfo(personId)
                    }.parserPerson(personId, isVirtual)
                }
            }
        )
    }

    /**
     * 取消或添加收藏
     */
    fun actionCollection(collect: Boolean) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                val collectType =
                    if (isVirtual) BgmPathType.TYPE_CHARACTER else BgmPathType.TYPE_PERSON

                val referer = BgmApiManager.buildReferer(collectType, personId)
                val gh = onPersonLiveData.value?.gh.orEmpty()

                onPersonLiveData.value = withContext(Dispatchers.IO) {
                    if (collect) {
                        BgmApiManager.bgmWebApi.postAddCollect(
                            referer = referer,
                            type = collectType,
                            id = personId,
                            gh = gh
                        )
                    } else {
                        BgmApiManager.bgmWebApi.postRemoveCollect(
                            referer = referer,
                            type = collectType,
                            id = personId,
                            gh = gh
                        )
                    }.parserPerson(personId, isVirtual)
                }

                // 刷新收藏
                UserHelper.notifyActionChange(collectType)
            }
        )
    }
}