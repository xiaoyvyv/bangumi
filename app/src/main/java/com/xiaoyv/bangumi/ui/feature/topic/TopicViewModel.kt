package com.xiaoyv.bangumi.ui.feature.topic

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.api.parser.impl.parserTopic
import com.xiaoyv.common.api.parser.impl.parserTopicEp
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TopicViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class TopicViewModel : BaseViewModel() {
    internal var topicId: String = ""

    @TopicType
    internal var topicType: String = TopicType.TYPE_EP

    internal val onTopicDetailLiveData = MutableLiveData<TopicDetailEntity?>()
    internal val onDeleteResult = MutableLiveData<Boolean>()

    private val requireTopicUserId: String
        get() = onTopicDetailLiveData.value?.userId.orEmpty()

    /**
     * 是否为自己的
     */
    internal val isMine: Boolean
        get() = requireTopicUserId.isNotBlank() && requireTopicUserId == UserHelper.currentUser.id

    fun queryTopicDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                val list = withContext(Dispatchers.IO) {
                    when (topicType) {
                        // 章节类型话题
                        TopicType.TYPE_EP -> BgmApiManager.bgmWebApi.queryEpDetail(topicId)
                            .parserTopicEp(topicId)
                        // 其它类型话题
                        else -> BgmApiManager.bgmWebApi.queryTopicDetail(topicId, topicType)
                            .parserTopic(topicId)
                    }
                }
                onTopicDetailLiveData.value = list
            }
        )
    }

    fun deleteTopic() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    when (topicType) {
                        TopicType.TYPE_GROUP -> {
                            BgmApiManager.bgmWebApi.deleteGrpTopic(topicId, UserHelper.formHash)
                        }

                        TopicType.TYPE_SUBJECT -> {
                            BgmApiManager.bgmWebApi.deleteSubjectTopic(topicId, UserHelper.formHash)
                        }

                        else -> throw IllegalArgumentException("暂不支持删除该类型")
                    }
                }
                onDeleteResult.value = true

                UserHelper.notifyActionChange(BgmPathType.TYPE_TOPIC)
            }
        )
    }
}