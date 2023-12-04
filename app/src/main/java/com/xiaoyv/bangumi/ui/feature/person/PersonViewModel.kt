package com.xiaoyv.bangumi.ui.feature.person

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.impl.parserPerson
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

    override fun onViewCreated() {
        queryPersonInfo()
    }

    private fun queryPersonInfo() {
        launchUI(
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
                    }.parserPerson(personId,isVirtual)
                }
            }
        )
    }
}