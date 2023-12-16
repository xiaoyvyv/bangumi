package com.xiaoyv.bangumi.ui.feature.post.blog

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.ui.feature.post.BasePostViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.impl.parserBlogDetail
import com.xiaoyv.common.api.parser.impl.parserBlogEditAttach
import com.xiaoyv.common.api.parser.impl.parserBlogEditInfo
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

/**
 * Class: [PostBlogViewModel]
 *
 * @author why
 * @since 12/2/23
 */
class PostBlogViewModel : BasePostViewModel() {
    /**
     * first 发布成功的 blogId
     * second 提示消息
     */
    internal val onPostBlogId = UnPeekLiveData<Pair<String?, String?>?>()

    /**
     * 编辑模式参数
     */
    internal var editModeBlogId = ""

    override fun onViewCreated() {
        if (isEditMode) {
            queryEditInfo()
        }
    }

    /**
     * 发表一篇日志
     */
    override fun sendPost(entity: CreatePostEntity) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onPostBlogId.value = (null to it.errorMsg)
            },
            block = {
                onPostBlogId.value = withContext(Dispatchers.IO) {
                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addForms(entity)
                        .build()

                    val detailEntity = BgmApiManager.bgmWebApi
                        .postCreateBlog(multipartBody)
                        .parserBlogDetail("")

                    if (detailEntity.id.isNotBlank()) {
                        return@withContext detailEntity.id to "发布成功"
                    }

                    throw IllegalStateException("发布失败！")
                }
            }
        )
    }

    /**
     * 保存编辑的一篇日志
     */
    override fun editPost(entity: CreatePostEntity) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                onPostBlogId.value = (null to it.errorMsg)
            },
            block = {
                onPostBlogId.value = withContext(Dispatchers.IO) {
                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addForms(entity)
                        .build()

                    val detailEntity = BgmApiManager.bgmWebApi
                        .postEditBlog(editModeBlogId, multipartBody)
                        .parserBlogDetail("")

                    if (detailEntity.id.isNotBlank()) {
                        return@withContext detailEntity.id to "修改成功"
                    }

                    throw IllegalStateException("修改失败！")
                }
            }
        )
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

    /**
     * 查询日志编辑的信息
     */
    private fun queryEditInfo() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                val (editInfo, attachInfo) = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi
                        .queryBlogEditInfo(editModeBlogId)
                        .let {
                            it.parserBlogEditInfo() to it.parserBlogEditAttach()
                        }
                }

                // 填充编辑历史
                onFillEditInfo.value = editInfo
                onAttachMediaList.value = attachInfo
                publicSend.value = editInfo.isPublic
            }
        )
    }
}