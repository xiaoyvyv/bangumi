package com.xiaoyv.bangumi.ui.feature.post.topic

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.ui.feature.post.BasePostViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.impl.parserBlogDetail
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BgmPathType
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

    internal var targetId: String = ""
    internal var groupOrMedia: Boolean = false

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
                onPostTopicId.value = withContext(Dispatchers.IO) {
                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addForms(entity)
                        .build()

                    // 发布目标类型
                    val targetType =
                        if (groupOrMedia) BgmPathType.TYPE_GROUP else BgmPathType.TYPE_SUBJECT

                    val detailEntity = BgmApiManager.bgmWebApi
                        .postCreateTopic(targetType, targetId, multipartBody)
                        .parserBlogDetail("")

                    if (detailEntity.id.isNotBlank()) {
                        return@withContext detailEntity.id to "发布成功"
                    }

                    throw IllegalStateException("发布失败！")
                }
            }
        )
    }

    override fun editPost(entity: CreatePostEntity) {

    }


    private fun MultipartBody.Builder.addForms(entity: CreatePostEntity): MultipartBody.Builder {
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入标题")
        }
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入内容")
        }

        // 自动添加媒体类型的 TAG
        val mediaTypeTag = GlobalConfig.mediaTypeName(requireAttach?.type.orEmpty())
        var postTags = entity.tags
        if (postTags.contains(mediaTypeTag).not()) {
            postTags = "$mediaTypeTag $postTags"
        }

        require(UserHelper.formHash.isNotBlank()) {
            "啊咧咧？参数丢失出错了！"
        }

        return this
            .addFormDataPart("formhash", UserHelper.formHash)
            .addFormDataPart("title", entity.title)
            .addFormDataPart("content", entity.content)
            .addFormDataPart("tags", postTags)
            .addFormDataPart("public", if (entity.isPublic) "1" else "0")
            .addFormDataPart("submit", "加上去")
            .apply {
                // 关联的条目
                onAttachMediaList.value.orEmpty().forEach { related ->
                    addFormDataPart("related_subject[]", related.id)
                }
            }
    }
}