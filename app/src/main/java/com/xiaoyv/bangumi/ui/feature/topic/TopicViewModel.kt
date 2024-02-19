package com.xiaoyv.bangumi.ui.feature.topic

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.api.parser.impl.parserTopic
import com.xiaoyv.common.api.parser.impl.parserTopicEp
import com.xiaoyv.common.api.parser.impl.parserTopicIndex
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.CollectionHelper
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
    internal var topicType: String = TopicType.TYPE_UNKNOWN
    internal var anchorCommentId: String? = null

    internal val onTopicDetailLiveData = MutableLiveData<TopicDetailEntity?>()
    internal val onDeleteResult = MutableLiveData<Boolean>()
    internal var isCollected = MutableLiveData(false)

    private val requireTopicUserId: String
        get() = onTopicDetailLiveData.value?.userId.orEmpty()

    /**
     * 是否为自己的
     */
    internal val isMine: Boolean
        get() = requireTopicUserId.isNotBlank() && requireTopicUserId == UserHelper.currentUser.id

    /**
     * 评论回复的表单
     */
    internal val replyForm: CommentFormEntity?
        get() = onTopicDetailLiveData.value?.replyForm

    fun queryTopicDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                val entity = withContext(Dispatchers.IO) {
                    when (topicType) {
                        // 目录单独处理
                        TopicType.TYPE_INDEX -> BgmApiManager.bgmWebApi.queryIndexComment(topicId)
                            .parserTopicIndex(topicId).anchoredComment()
                        // 章节类型话题
                        TopicType.TYPE_EP -> BgmApiManager.bgmWebApi.queryEpDetail(topicId)
                            .parserTopicEp(topicId).anchoredComment()
                        // 其它类型话题
                        else -> BgmApiManager.bgmWebApi.queryTopicDetail(topicId, topicType)
                            .parserTopic(topicId).anchoredComment()
                    }
                }
                entity.anchorCommentId = anchorCommentId.orEmpty()
                isCollected.value = CollectionHelper.isCollected(topicId, LocalCollectionType.TYPE_TOPIC)
                onTopicDetailLiveData.value = entity
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

    /**
     * 开关收藏
     */
    fun toggleCollection() {
        val topicEntity = onTopicDetailLiveData.value ?: return
        launchUI {
            if (isCollected.value == true) {
                CollectionHelper.deleteCollect(topicId, LocalCollectionType.TYPE_TOPIC)
                isCollected.value = false
            } else {
                CollectionHelper.saveTopic(topicEntity, topicType)
                isCollected.value = true
            }
        }
    }

    private fun TopicDetailEntity.anchoredComment(): TopicDetailEntity {
        if (anchorCommentId.isNullOrBlank()) return this
        for (i in comments.indices) {
            val comment = comments[i]
            if (comment.id == anchorCommentId) {
                comment.anchored = true
                break
            }

            for (j in comment.topicSubReply.indices) {
                val subComment = comment.topicSubReply[j]
                if (subComment.id == anchorCommentId) {
                    subComment.anchored = true
                    break
                }
            }
        }
        return this
    }
}
