package com.xiaoyv.bangumi.ui.discover.group.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.GroupDetailEntity
import com.xiaoyv.common.api.parser.impl.parserGroupDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [GroupDetailViewModel]
 *
 * @author why
 * @since 12/7/23
 */
class GroupDetailViewModel : BaseViewModel() {
    internal var groupId: String = ""
    internal val onGroupDetailLiveData = MutableLiveData<GroupDetailEntity?>()

    override fun onViewCreated() {
        queryGroupDetail()
    }

    private fun queryGroupDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                onGroupDetailLiveData.value = null
            },
            block = {
                onGroupDetailLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryGroupDetail(groupId).parserGroupDetail(groupId)
                }
            }
        )
    }
}