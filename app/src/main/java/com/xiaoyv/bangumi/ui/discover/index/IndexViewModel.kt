package com.xiaoyv.bangumi.ui.discover.index

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserIndex
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [IndexViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class IndexViewModel : BaseViewModel() {
    internal val onItemLiveData = MutableLiveData<List<IndexAdapter.IndexItem>?>()

    fun queryIndexHome() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                onItemLiveData.value = withContext(Dispatchers.IO) {
                    val items = arrayListOf<IndexAdapter.IndexItem>()
                    val indexEntity = BgmApiManager.bgmWebApi.queryIndexPage().parserIndex()

                    debugLog { indexEntity.toJson(true) }

                    items.addAll(indexEntity.gridItems.map {
                        IndexAdapter.IndexItem(IndexAdapter.TYPE_GRID, it)
                    })
                    items.add(
                        IndexAdapter.IndexItem(
                            type = IndexAdapter.TYPE_TITLE,
                            entity = "热门目录" to false,
                        )
                    )
                    items.addAll(indexEntity.hotItems.map {
                        IndexAdapter.IndexItem(IndexAdapter.TYPE_ITEM, it)
                    })
                    items.add(
                        IndexAdapter.IndexItem(
                            type = IndexAdapter.TYPE_TITLE,
                            entity = "最新创建的目录" to true,
                        )
                    )
                    items.addAll(indexEntity.newItems.map {
                        IndexAdapter.IndexItem(IndexAdapter.TYPE_ITEM, it)
                    })
                    items
                }
            }
        )
    }

}