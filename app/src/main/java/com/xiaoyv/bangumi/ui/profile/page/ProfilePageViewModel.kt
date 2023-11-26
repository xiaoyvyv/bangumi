package com.xiaoyv.bangumi.ui.profile.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.api.BgmWebApi
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.impl.TimeParser.parserTimelineForms
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.bean.ProfileTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [ProfilePageViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class ProfilePageViewModel : BaseViewModel() {
    internal var rprofileTab: ProfileTab? = null

    internal val userId: String? = null

    internal val profileType: String
        get() = rprofileTab?.type ?: ProfileType.TYPE_COLLECTION

    internal val onTimelineLiveData = MutableLiveData<List<TimelineEntity>?>()

    fun queryTimeline() {
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