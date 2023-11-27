package com.xiaoyv.bangumi.ui.profile.edit

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.api.parser.impl.parserSettingUpdateResult
import com.xiaoyv.common.config.annotation.FormInputType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.copyOf
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.min

/**
 * Class: [EditProfileViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class EditProfileViewModel : BaseViewModel() {
    internal val onEditOptionLiveData = MutableLiveData<List<SettingBaseEntity>?>()

    /**
     * 更新结果
     */
    internal val onActionResultLiveData = UnPeekLiveData<String?>()

    /**
     * 选取文件的条目
     */
    var selectFileItem: SettingBaseEntity? = null

    override fun onViewCreated() {
        queryUserInfo()
    }

    fun queryUserInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onEditOptionLiveData.value = null
            },
            block = {
                onEditOptionLiveData.value = withContext(Dispatchers.IO) {
                    UserHelper.refresh()
                }
            }
        )
    }

    /**
     * 更新设置
     */
    fun updateSettings() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onActionResultLiveData.value = it.message
            },
            block = {
                onActionResultLiveData.value = withContext(Dispatchers.IO) {
                    val options = onEditOptionLiveData.value.orEmpty()
                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addForms(options)
                        .build()

                    val updateResult =
                        BgmApiManager.bgmWebApi.updateSettings(multipartBody)
                            .parserSettingUpdateResult()

                    updateResult
                }
            }
        )
    }

    /**
     * 压缩图片
     */
    fun compressAndCropAvatar(uri: Uri) {
        launchUI(error = { onActionResultLiveData.value = it.message }) {
            withContext(Dispatchers.IO) {
                val file = UriUtils.uri2File(uri)
                if (file.length() > MemoryConstants.MB * 5) {
                    throw IllegalArgumentException("请选择小于 5M 的图片")
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val width = bitmap.width
                val height = bitmap.height
                val newSize = min(width, height)
                val x = (width - newSize) / 2
                val y = (height - newSize) / 2
                val clipAvatar = ImageUtils.clip(bitmap, x, y, newSize, newSize, true)
                val tmpDir = PathUtils.getCachePathExternalFirst() + "/avatar"
                val tmp = tmpDir + "/${System.currentTimeMillis()}.png"
                val quality =
                    ImageUtils.compressByQuality(clipAvatar, MemoryConstants.KB * 500L, true)

                FileUtils.deleteAllInDir(tmpDir)
                FileIOUtils.writeFileFromBytesByStream(tmp, quality)

                val entities = onEditOptionLiveData.value.orEmpty()
                val fileItem = selectFileItem
                if (fileItem != null) {
                    val targetItem = entities.find { it.field == fileItem.field }
                    if (targetItem != null) {
                        targetItem.value = tmp
                        onEditOptionLiveData.sendValue(entities.copyOf())
                    }

                    // reset
                    selectFileItem = null
                }
            }
        }
    }

    /**
     * 添加参数
     */
    private fun MultipartBody.Builder.addForms(options: List<SettingBaseEntity>): MultipartBody.Builder {
        options.forEach {
            val type = it.type
            val field = it.field
            val value = it.value

            when (type) {
                FormInputType.TYPE_FILE -> {
                    val mediaType = "image/png".toMediaType()
                    val avatarFile = FileUtils.getFileByPath(value)
                    if (FileUtils.isFileExists(avatarFile)) {
                        val fileBody = avatarFile.asRequestBody(mediaType)
                        addFormDataPart(field, avatarFile.name, fileBody)
                    } else {
                        addFormDataPart(field, "", "".toRequestBody(mediaType))
                    }
                }

                else -> {
                    addFormDataPart(field, value)
                }
            }
        }
        return this
    }

    /**
     * 刷新某选项
     */
    fun refreshOptionItem(newItem: SettingBaseEntity) {
        val entities = onEditOptionLiveData.value.orEmpty()
        entities.find { it.field == newItem.field }?.setNewValue(newItem)
        onEditOptionLiveData.value = entities.copyOf()
    }

    /**
     * UI 层面不显示隐藏表单和提交表单
     */
    fun optItemSort(it: List<SettingBaseEntity>): List<SettingBaseEntity> {
        val entities = it.copyOf()
            .filter { entity -> entity.type != FormInputType.TYPE_HIDDEN && entity.type != FormInputType.TYPE_SUBMIT }
            .sortedBy {
                if (it.type == FormInputType.TYPE_FILE) 0 else 1
            }

        return entities
    }
}

