package com.xiaoyv.bangumi.ui.feature.setting.network

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [NetworkConfigViewModel]
 *
 * @author why
 * @since 1/4/24
 */
class NetworkConfigViewModel : BaseViewModel() {

    /**
     * 测试网络连通性
     */
    fun testNetwork(url: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.test(url)
                }
                showToastCompat("HTTP: ${response.code()}")
            }
        )
    }
}