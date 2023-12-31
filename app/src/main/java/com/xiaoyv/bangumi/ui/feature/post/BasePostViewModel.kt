package com.xiaoyv.bangumi.ui.feature.post

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.ImageHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * Class: [BasePostViewModel]
 *
 * @author why
 * @since 12/8/23
 */
open class BasePostViewModel : BaseViewModel() {
    internal val onUploadImageResult = MutableLiveData<String?>()
    internal val onAttachMediaList = MutableLiveData<List<PostAttach>>()

    /**
     * 编辑模式下，进入填充旧的数据
     */
    internal val onFillEditInfo = MutableLiveData<CreatePostEntity>()

    /**
     * 是否公开
     */
    internal val publicSend = MutableLiveData(true)

    /**
     * 获取第一个 Attach
     */
    internal val requireAttach: PostAttach?
        get() = onAttachMediaList.value?.firstOrNull()

    /**
     * 编辑模式参数
     */
    internal var isEditMode = false

    /**
     * 压缩上传图片
     */
    fun compressAndUpload(uri: Uri) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                onUploadImageResult.value = withContext(Dispatchers.IO) {
                    val file = UriUtils.uri2File(uri)
                    if (file.length() > MemoryConstants.MB * 5) {
                        throw IllegalArgumentException("请选择小于 5M 的图片")
                    }

                    if (ConfigHelper.isImageCompress) {
                        debugLog { "压缩图片：$file" }
                        // 压缩图片、开始上传
                        uploadImage(ImageHelper.compressImage(file))
                    } else {
                        debugLog { "跳过压缩：$file" }

                        // 开始上传
                        uploadImage(file.absolutePath)
                    }
                }
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
     * 添加一个关联
     */
    fun addAttach(mediaAttach: PostAttach) {
        val sampleAvatars = onAttachMediaList.value.orEmpty().toMutableList()
        if (sampleAvatars.contains(mediaAttach).not()) {
            sampleAvatars.add(mediaAttach)
            onAttachMediaList.value = sampleAvatars
        }
    }

    /**
     * 移除一个关联
     */
    fun removeAttach(mediaAttach: PostAttach) {
        val sampleAvatars = onAttachMediaList.value.orEmpty().toMutableList()
        sampleAvatars.remove(mediaAttach)
        onAttachMediaList.value = sampleAvatars
    }

    open fun sendPost(entity: CreatePostEntity) {}
    open fun editPost(entity: CreatePostEntity) {}
}