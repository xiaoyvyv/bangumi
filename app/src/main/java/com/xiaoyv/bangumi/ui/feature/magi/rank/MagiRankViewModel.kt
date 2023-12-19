package com.xiaoyv.bangumi.ui.feature.magi.rank

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserMagiRank
import com.xiaoyv.common.config.bean.AdapterTypeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MagiRankViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MagiRankViewModel : BaseViewModel() {
    internal val onItemLiveData = MutableLiveData<List<AdapterTypeItem>?>()

    fun querySyncRateRank() {
        launchUI(
            stateView = loadingViewState,
            error = {

            },
            block = {
                onItemLiveData.value = withContext(Dispatchers.IO) {
                    val items = arrayListOf<AdapterTypeItem>()
                    val magiRank = BgmApiManager.bgmWebApi.queryMagiRank().parserMagiRank()
                    items.add(
                        AdapterTypeItem(
                            type = MagiRankAdapter.TYPE_HEADER,
                            entity = "同步率总排行",
                        )
                    )
                    items.addAll(magiRank.rateRank.map {
                        it.challenge = true
                        AdapterTypeItem(type = MagiRankAdapter.TYPE_ITEM, entity = it)
                    })
                    items.add(
                        AdapterTypeItem(
                            type = MagiRankAdapter.TYPE_HEADER,
                            entity = "创建总排行",
                        )
                    )
                    items.addAll(magiRank.createRank.map {
                        it.challenge = false
                        AdapterTypeItem(type = MagiRankAdapter.TYPE_ITEM, entity = it)
                    })
                    items
                }
            }
        )
    }
}