package com.xiaoyv.common.widget.reply

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.UriUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.impl.parserReplyParam
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * Class: [ReplyCommentViewModel]
 *
 * @author why
 * @since 12/30/23
 */
class ReplyCommentViewModel : BaseViewModel() {
    internal val onReplyLiveData = MutableLiveData<ReplyResultEntity>()
    internal val onUploadImageResult = MutableLiveData<String?>()

    private val cacheStoreName = "comment"

    /**
     * 评论等参数
     */
    internal var replyForm: CommentFormEntity? = null
    internal var targetComment: CommentTreeEntity? = null
    internal var replyJs: String = ""

    private val cacheStore get() = SPUtils.getInstance(cacheStoreName)

    /**
     * 缓存 Key
     */
    private val cacheKey: String
        get() = if (replyJs.isBlank()) "" else EncryptUtils.encryptMD5ToString(replyJs)

    /**
     * 读取缓存的评论
     */
    internal var comment: String
        get() = if (cacheKey.isNotBlank()) cacheStore.getString(cacheKey, "") else ""
        set(value) = cacheStore.put(cacheKey, value)

    /**
     * 处理图片
     */
    fun handleImagePicture(uri: Uri) {
        launchUI(
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)

                loadingViewState.showContent()
            },
            block = {
                loadingViewState.showLoading()

                onUploadImageResult.value = withContext(Dispatchers.IO) {
                    val file = UriUtils.uri2File(uri)
                    if (file.length() > MemoryConstants.MB * 5) {
                        throw IllegalArgumentException("请选择小于 5M 的图片")
                    }

                    if (ConfigHelper.isImageCompress) {
                        debugLog { "压缩图片：$file" }

                        // 压缩图片
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        val tmpDir = PathUtils.getCachePathExternalFirst() + "/image"
                        val tmp = tmpDir + "/${System.currentTimeMillis()}.png"
                        val quality =
                            ImageUtils.compressByQuality(bitmap, MemoryConstants.KB * 500L, true)

                        FileUtils.deleteAllInDir(tmpDir)
                        FileIOUtils.writeFileFromBytesByStream(tmp, quality)

                        // 开始上传
                        uploadImage(tmp)
                    } else {
                        debugLog { "跳过压缩：$file" }

                        // 开始上传
                        uploadImage(file.absolutePath)
                    }
                }

                loadingViewState.showContent()
            }
        )
    }

    /**
     * 上传图片，获取链接
     */
    private suspend fun uploadImage(tmp: String): String {
        val file = FileUtils.getFileByPath(tmp)
        val blogImage = BgmApiManager.bgmWebApi.uploadBlogImage(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
        return blogImage.thumbUrl.orEmpty().optImageUrl(largest = true)
    }

    /**
     * 发送评论
     */
    internal fun sendReply(input: String) {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

            },
            block = {
                val replyForm = requireNotNull(replyForm)
                val replyParam = replyJs.parserReplyParam()
                val replyResult: ReplyResultEntity = withContext(Dispatchers.IO) {
                    val stringMap = mutableMapOf(
                        "topic_id" to replyParam.topicId.toString(),
                        "related" to replyParam.postId.toString(),
                        "sub_reply_uid" to replyParam.subReplyUid.toString(),
                        "post_uid" to replyParam.postUid.toString(),
                        "related_photo" to "0",
                        "content" to input,
                    )

                    // 隐藏表单
                    stringMap.putAll(replyForm.inputs)

                    // 发布结果
                    BgmApiManager.bgmWebApi.postReply(
                        submitAction = replyForm.action,
                        param = stringMap
                    )
                }

                // 评论结果
                comment = ""
                onReplyLiveData.value = replyResult
            }
        )
    }

    /**
     * 回复提示
     */
    internal fun buildReplyHint(): String {
        val targetComment = targetComment ?: return ""

        // 显示回复的目标评论部分内容
        val replyContent = targetComment.replyContent.parseHtml().toString()
        val summaryHint = if (replyContent.length > 20) {
            replyContent.substring(0, 20) + "..."
        } else {
            replyContent
        }

        return String.format("回复 %s：%s", targetComment.userName, summaryHint)
    }
}