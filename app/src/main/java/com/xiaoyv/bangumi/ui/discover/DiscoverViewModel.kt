package com.xiaoyv.bangumi.ui.discover

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserOnlineCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [DiscoverViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class DiscoverViewModel : BaseViewModel() {
    internal val onOnlineCountLiveData = MutableLiveData(0)

    override fun onViewCreated() {
        super.onViewCreated()

        queryOnlineCount()
    }

    fun queryOnlineCount(showLoading: Boolean = false) {
        launchUI(state = if (showLoading) loadingDialogState(cancelable = false) else null) {
            onOnlineCountLiveData.value = withContext(Dispatchers.IO) {
                BgmApiManager.bgmWebApi.queryHomePage().parserOnlineCount()
            }
        }
    }
}