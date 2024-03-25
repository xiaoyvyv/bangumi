@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.api.parser.impl.parserSuperTopic
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.config.bean.tab.SuperTopicTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [RakuenPageViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenPageViewModel : BaseViewModel() {
    internal val onSuperTopicLiveData = MutableLiveData<List<SuperTopicEntity>?>()

    internal var topicTab: SuperTopicTab? = null

    private val superTopicType: String
        get() = topicTab?.type ?: SuperType.TYPE_ALL

    private var superTopicFilter = ""

    /**
     * 是否为小组类型的超展开
     */
    internal val isGroupType
        get() = topicTab?.type == SuperType.TYPE_GROUP || topicTab?.type == SuperType.TYPE_MY_GROUP

    fun queryTimeline() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                onSuperTopicLiveData.value = null
            },
            block = {
                onSuperTopicLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.querySuperTopic(
                        type = superTopicType,
                        filter = superTopicFilter
                    ).parserSuperTopic()
                }
            }
        )
    }
}