package com.xiaoyv.bangumi.ui.discover.home

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.CacheDiskUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.api.parser.impl.HomeParser.parserHomePage
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
                    BgmApiManager.bgmJsonApi.queryMainPage().parserHomePage()
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