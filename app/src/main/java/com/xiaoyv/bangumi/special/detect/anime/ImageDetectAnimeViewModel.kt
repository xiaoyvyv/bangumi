package com.xiaoyv.bangumi.special.detect.anime

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.xiaoyv.bangumi.special.detect.ImageDetectViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.anime.AnimeSourceEntity
import com.xiaoyv.common.helper.ImageHelper
import com.xiaoyv.common.widget.setting.SearchOptionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * ImageDetectAnimeViewModel
 *
 * @author why
 * @since 11/18/23
 */
class ImageDetectAnimeViewModel : ImageDetectViewModel() {
    internal val onAnimeSourceLiveData = MutableLiveData<AnimeSourceEntity?>()


    override fun onImageSelected(imageUri: Uri, selectedOptions: List<SearchOptionView.Option>) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onAnimeSourceLiveData.value = null
            },
            block = {
                onAnimeSourceLiveData.value = withContext(Dispatchers.IO) {
                    val targetFile = FileUtils.getFileByPath(ImageHelper.compressImage(imageUri))
                    val requestBody = targetFile.asRequestBody("multipart/form-data".toMediaType())
                    val body = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
                    BgmApiManager.bgmJsonApi.queryAnimeByImage(body)
                }
            }
        )
    }
}