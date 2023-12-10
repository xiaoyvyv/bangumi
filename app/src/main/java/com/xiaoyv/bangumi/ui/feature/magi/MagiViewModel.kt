package com.xiaoyv.bangumi.ui.feature.magi

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI

/**
 * Class: [MagiViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MagiViewModel : BaseViewModel() {


    fun queryUserInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                /*   onTimelineLiveData.value = withContext(Dispatchers.IO) {
                       BgmApiManager.bgmWebApi.queryTimeline(
                           path = BgmWebApi.timelineUrl(userId),
                           type = profileType
                       ).parserTimelineForms()
                   }*/
            }
        )
    }
}