package com.xiaoyv.bangumi.ui.profile

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI

/**
 * Class: [ProfileViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class ProfileViewModel : BaseViewModel() {


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