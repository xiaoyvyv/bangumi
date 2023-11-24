package com.xiaoyv.bangumi.ui.home

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.HomeParser.parserHomePage
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [HomeViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class HomeViewModel : BaseViewModel() {
    internal val onHomeIndexLiveData = MutableLiveData<HomeIndexEntity?>()

    override fun onViewCreated() {
        queryHomeCardImage()
    }

    private fun queryHomeCardImage() {
        launchUI(
            error = { it.printStackTrace() },
            block = {
                onHomeIndexLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryMainPage().parserHomePage()
                }

//                debugLog(document.text())
            }
        )
    }
}