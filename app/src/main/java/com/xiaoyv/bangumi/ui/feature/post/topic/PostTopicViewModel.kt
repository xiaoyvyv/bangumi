package com.xiaoyv.bangumi.ui.feature.post.topic

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.ui.feature.post.BasePostViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.impl.parserTopicSendResult
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

/**
 * Class: [PostTopicViewModel]
 *
 * @author why
 * @since 12/8/23
 */
open class PostTopicViewModel : BasePostViewModel() {
    /**
     * first 发布成功的 topicId
     * second 提示消息
     */
    internal val onPostTopicId = UnPeekLiveData<Pair<String?, String?>?>()

    internal var targetEditId = ""

    internal val targetId
        get() = requireAttach?.id.orEmpty()

    @TopicType
    internal val topicType: String
        get() = requireAttach?.type.orEmpty()

    /**
     * 发表话题
     */
    override fun sendPost(entity: CreatePostEntity) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onPostTopicId.value = (null to it.errorMsg)
            },
            block = {
                require(targetId.isNotBlank() && targetId != GlobalConfig.GROUP_MY_SEND_TOPIC && targetId != GlobalConfig.GROUP_MY_REPLY_TOPIC) {
                    "请先选择一个讨论的条目或小组"
                }

                onPostTopicId.value = withContext(Dispatchers.IO) {
                    val map = generateMap(entity)

                    val topicId = BgmApiManager.bgmWebApi
                        .postCreateTopic(topicType, targetId, map)
                        .parserTopicSendResult()

                    if (topicId.isNotBlank()) {
                        return@withContext topicId to "发布成功"
                    }

                    throw IllegalStateException("发布失败！")
                }
            }
        )
    }

    override fun editPost(entity: CreatePostEntity) {

    }


    private fun generateMap(entity: CreatePostEntity): Map<String, String> {
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入标题")
        }
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入内容")
        }

        require(UserHelper.formHash.isNotBlank()) { "啊咧咧？参数丢失出错了！" }

       return mapOf(
            "formhash" to UserHelper.formHash,
            "title" to entity.title,
            "content" to entity.content,
            "mode" to "rakuen",
            "submit" to "加上去"
        )
    }
}