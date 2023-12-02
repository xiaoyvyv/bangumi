package com.xiaoyv.bangumi.ui.feature.post

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.TimeUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogCreateEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserBlogDetail
import com.xiaoyv.common.api.parser.impl.parserCreateBlog
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.common.widget.web.page.BlogPostView
import com.xiaoyv.widget.kts.useNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

/**
 * Class: [PostViewModel]
 *
 * @author why
 * @since 12/2/23
 */
class PostViewModel : BaseViewModel() {
    internal val onCreateEntity = MutableLiveData<BlogCreateEntity>()

    /**
     * first 发布成功的 blogId
     * second 提示消息
     */
    internal val onPostBlogId = UnPeekLiveData<Pair<String?, String?>?>()
    internal var mediaEntity: MediaDetailEntity? = null

    private val mediaId: String?
        get() = mediaEntity?.id

    fun queryPostFormHash(webBase: WebBase) {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                val deferred = async(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryCreateBlogForm(mediaId).parserCreateBlog()
                }
                val web = async { webBase.waitMounted() }

                onCreateEntity.value = deferred.await().apply {
                    pageName = "发表日志"
                    userName = UserHelper.currentUser.nickname.orEmpty()
                    time = TimeUtils.getNowString()
                    related = buildRelated()
                }
                web.await()
            }
        )
    }

    private fun buildRelated(): List<MediaDetailEntity.MediaRelative> {
        return arrayListOf<MediaDetailEntity.MediaRelative>().apply {
            useNotNull(mediaEntity) {
                add(
                    MediaDetailEntity.MediaRelative(
                        id = id,
                        titleCn = titleCn,
                        titleNative = titleNative,
                        cover = cover
                    )
                )
            }
        }
    }

    fun sendPost(webBase: BlogPostView) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onPostBlogId.value = (null to it.message)
            },
            block = {
                val createEntity = requireNotNull(webBase.getPostInfo())
                onPostBlogId.value = withContext(Dispatchers.IO) {
                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addForms(createEntity)
                        .build()

                    val detailEntity = BgmApiManager.bgmWebApi.postCreateBlog(multipartBody)
                        .parserBlogDetail("")
                    if (detailEntity.id.isNotBlank()) {
                        return@withContext detailEntity.id to "发布成功"
                    }

                    throw IllegalStateException("发布失败！")
                }
            }
        )
    }

    private fun MultipartBody.Builder.addForms(entity: BlogCreateEntity): MultipartBody.Builder {
        if (entity.formHash.isBlank()) {
            throw IllegalArgumentException("请退出页面后重新进入")
        }
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入标题后再发布")
        }
        if (entity.title.isBlank()) {
            throw IllegalArgumentException("请输入内容后再发布")
        }

        return this
            .addFormDataPart("formhash", entity.formHash)
            .addFormDataPart("title", entity.title)
            .addFormDataPart("content", entity.content)
            .addFormDataPart("tags", entity.tags)
            .addFormDataPart("public", if (entity.isPublic) "0" else "1")
            .addFormDataPart("submit", "加上去")
            .apply {
                entity.related.forEach { related ->
                    addFormDataPart("related_subject[]", related.id)
                }
            }
    }
}