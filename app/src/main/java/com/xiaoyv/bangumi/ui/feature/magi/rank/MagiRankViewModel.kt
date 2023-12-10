package com.xiaoyv.bangumi.ui.feature.magi.rank

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.bean.ProfileTab

/**
 * Class: [MagiRankViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MagiRankViewModel : BaseViewModel() {
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