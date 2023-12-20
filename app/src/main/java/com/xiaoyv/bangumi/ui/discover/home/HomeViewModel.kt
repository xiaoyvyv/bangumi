package com.xiaoyv.bangumi.ui.discover.home

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.CacheDiskUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.api.parser.impl.HomeParser.parserHomePage
import com.xiaoyv.common.config.annotation.HomeFeatureType
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.parcelableCreator
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
                readCache()
            },
            block = {
                readCache()
                onHomeIndexLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryMainPage().parserHomePage().apply {
                        banner.features = listOf(
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_MAGI,
                                title = "MAGI 问答",
                                icon = CommonDrawable.ic_magi
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_ANIME_PICTURES,
                                title = "A-P 图站",
                                icon = CommonDrawable.ic_format_image
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_SEARCH,
                                title = "搜索",
                                icon = CommonDrawable.ic_search
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_ALMANAC,
                                title = "年鉴",
                                icon = CommonDrawable.ic_calendar
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_EMAIL,
                                title = "短信",
                                icon = CommonDrawable.ic_email_normal
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_DOLLARS,
                                title = "Dollars",
                                icon = CommonDrawable.ic_dollars
                            ),
                            HomeIndexFeature(
                                id = HomeFeatureType.TYPE_WIKI,
                                title = "WIKI",
                                icon = CommonDrawable.ic_wiki
                            ),
                        )
                    }
                }
                saveCache()
            }
        )
    }

    private fun readCache() {
        runCatching {
            val cache = CacheDiskUtils.getInstance()
                .getParcelable(javaClass.simpleName, parcelableCreator<HomeIndexEntity>())
            if (cache != onHomeIndexLiveData.value) {
                onHomeIndexLiveData.value = cache
            }
        }
    }

    private fun saveCache() {
        CacheDiskUtils.getInstance().put(javaClass.simpleName, onHomeIndexLiveData.value)
    }
}