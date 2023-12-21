package com.xiaoyv.bangumi.ui.discover.mono

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserMono
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.UserHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MonoViewModel]
 *
 * @author why
 * @since 12/19/23
 */
class MonoViewModel : BaseViewModel() {
    internal val onMonoIndexLiveData = MutableLiveData<List<AdapterTypeItem>?>()

    internal var userId: String = ""
    internal var requireLogin: Boolean = false

    fun queryMono() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onMonoIndexLiveData.value = null
            },
            block = {
                onMonoIndexLiveData.value = withContext(Dispatchers.IO) {
                    if (requireLogin) require(UserHelper.isLogin) { "你还没有登录呢" }

                    val document =
                        if (userId.isNotBlank()) BgmApiManager.bgmWebApi.queryUserMono(userId)
                        else BgmApiManager.bgmWebApi.queryMonoIndex()

                    document.parserMono().let {
                        it.grids.flatMap { grid ->
                            val items = mutableListOf<AdapterTypeItem>()
                            if (grid.items.isNotEmpty()) {
                                items.add(
                                    AdapterTypeItem(
                                        title = grid.title,
                                        entity = grid.items.first().type,
                                        type = MonoAdapter.TYPE_TITLE
                                    )
                                )
                                items.addAll(grid.items.map { item ->
                                    AdapterTypeItem(
                                        entity = item,
                                        type = MonoAdapter.TYPE_GRID
                                    )
                                })
                            }
                            items
                        }
                    }.apply {
                        require(isNotEmpty()) { "暂时没有人物数据" }
                    }
                }
            }
        )
    }
}