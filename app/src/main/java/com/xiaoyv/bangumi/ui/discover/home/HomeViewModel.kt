package com.xiaoyv.bangumi.ui.discover.home

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.api.parser.impl.parserHomePageWithoutLogin
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.kts.CommonDrawable
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
            error = {
                it.printStackTrace()
                onHomeIndexLiveData.value = CacheHelper.cacheHome
            },
            block = {
                onHomeIndexLiveData.value = withContext(Dispatchers.IO) {
                    val cacheHome = CacheHelper.cacheHome
                    cacheHome
                }
                onHomeIndexLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryMainPage().parserHomePageWithoutLogin().apply {
                        banner.features = listOf(
                            HomeIndexFeature(
                                id = FeatureType.TYPE_MAGI,
                                title = "MAGI 问答",
                                icon = CommonDrawable.ic_magi
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_ANIME_PICTURES,
                                title = "A-P 图站",
                                icon = CommonDrawable.ic_format_image
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_SEARCH,
                                title = "搜索",
                                icon = CommonDrawable.ic_search
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_RANK,
                                title = "排行榜",
                                icon = CommonDrawable.ic_bottom_rank
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_PROCESS,
                                title = "进度管理",
                                icon = CommonDrawable.ic_process
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_ALMANAC,
                                title = "年鉴",
                                icon = CommonDrawable.ic_calendar
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_EMAIL,
                                title = "短信",
                                icon = CommonDrawable.ic_email_normal
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_DETECT_ANIME,
                                title = "以图搜番",
                                icon = CommonDrawable.ic_image_search
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_DETECT_CHARACTER,
                                title = "以图识人",
                                icon = CommonDrawable.ic_person_search
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_DOLLARS,
                                title = "Dollars",
                                icon = CommonDrawable.ic_dollars
                            ),
                            HomeIndexFeature(
                                id = FeatureType.TYPE_WIKI,
                                title = "WIKI",
                                icon = CommonDrawable.ic_wiki
                            ),
                        )
                    }.apply {
                        CacheHelper.cacheHome = this
                    }
                }
            }
        )
    }
}