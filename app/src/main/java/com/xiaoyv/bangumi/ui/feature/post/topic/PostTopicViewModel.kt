package com.xiaoyv.bangumi.ui.feature.post.topic

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.ui.feature.post.BasePostViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.impl.parserGroupDetail
import com.xiaoyv.common.api.parser.impl.parserTopicSendResult
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    private val targetId
        get() = requireAttach?.id.orEmpty()

    /**
     * 发布话题，这里目前都是小组类型，章节和人物暂时没有支持
     */
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
                // 自己的回复不能发布
                require(targetId.isNotBlank() && targetId != GlobalConfig.GROUP_MY_SEND_TOPIC && targetId != GlobalConfig.GROUP_MY_REPLY_TOPIC) {
                    "请先选择一个讨论的条目或小组"
                }

                // 判断是否为小组的ID，是的话需要获取数字小组 ID 才能发布
                var sendTargetId = targetId
                if (topicType == TopicType.TYPE_GROUP) {
                    sendTargetId = fetchGroupNumberIdById(targetId)
                }

                // 发布
                onPostTopicId.value = withContext(Dispatchers.IO) {
                    val map = generateMap(entity)

                    val topicId = BgmApiManager.bgmWebApi
                        .postCreateTopic(topicType, sendTargetId, map)
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

    /**
     * 构建发布参数
     */
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

    /**
     * 获取小组的数字ID，用于发布话题，登录后才可以获取
     */
    private suspend fun fetchGroupNumberIdById(groupId: String): String {
        return withContext(Dispatchers.IO) {
            require(UserHelper.isLogin) { "你还没有登录呢" }
            val groupDetail = BgmApiManager.bgmWebApi.queryGroupDetail(groupId)
                .parserGroupDetail(groupId)

            // 半公开小组，必须加入才可以发言
            if (groupDetail.isJoin.not() && groupDetail.groupNumberId.isBlank()) {
                throw IllegalArgumentException("小组是半公开小组，只有小组成员才能发言")
            }

            require(groupDetail.groupNumberId.isNotBlank()) { "小组ID查询失败" }

            return@withContext groupDetail.groupNumberId
        }
    }
}