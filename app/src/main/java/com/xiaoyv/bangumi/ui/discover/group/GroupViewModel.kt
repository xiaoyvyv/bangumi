package com.xiaoyv.bangumi.ui.discover.group

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserGroupIndex
import com.xiaoyv.common.config.bean.AdapterTypeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [GroupViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class GroupViewModel : BaseViewModel() {
    internal val onGroupIndexLiveData = MutableLiveData<List<AdapterTypeItem>?>()

    fun queryGroupIndex() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onGroupIndexLiveData.value = null
            },
            block = {
                onGroupIndexLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryGroupIndex().parserGroupIndex().let {
                        listOf(
                            AdapterTypeItem(
                                type = GroupAdapter.TYPE_GROUP_HOT,
                                title = "热门小组",
                                entity = it.hotGroups
                            ),
                            AdapterTypeItem(
                                type = GroupAdapter.TYPE_GROUP_NEW,
                                title = "最新的小组",
                                entity = it.newGroups
                            ),
                            AdapterTypeItem(
                                type = GroupAdapter.TYPE_TOPIC,
                                title = "最新的话题",
                                entity = it.hotTopics
                            )
                        )
                    }
                }
            }
        )
    }
}