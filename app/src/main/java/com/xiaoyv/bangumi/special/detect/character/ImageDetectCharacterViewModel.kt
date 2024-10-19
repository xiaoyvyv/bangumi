package com.xiaoyv.bangumi.special.detect.character

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.xiaoyv.bangumi.special.detect.ImageDetectViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.anime.DetectCharacterEntity
import com.xiaoyv.common.api.response.anime.ListParcelableWrapEntity
import com.xiaoyv.common.helper.ImageHelper
import com.xiaoyv.common.helper.ImageProcessor
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.widget.setting.SearchOptionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * ImageDetectCharacterViewModel
 *
 * @author why
 * @since 11/21/23
 */
class ImageDetectCharacterViewModel : ImageDetectViewModel() {
    internal val onDetectCharacterLiveData =
        MutableLiveData<ListParcelableWrapEntity<DetectCharacterEntity>?>()

    private val imageProcessor by lazy { ImageProcessor() }

    override val searchOptions: List<SearchOptionView.Option>
        get() = listOf(
            SearchOptionView.Option(
                name = i18n(CommonString.detect_model),
                fieldName = "model",
                options = listOf(
                    SearchOptionView.OptionItem(
                        name = i18n(CommonString.detect_model_anime),
                        value = "anime"
                    ),
                    SearchOptionView.OptionItem(
                        name = i18n(CommonString.detect_model_anime1),
                        value = "anime_model_lovelive"
                    ),
                    SearchOptionView.OptionItem(
                        name = i18n(CommonString.detect_model_anime2),
                        value = "pre_stable"
                    ),
                    SearchOptionView.OptionItem(
                        name = i18n(CommonString.detect_model_gal),
                        value = "game"
                    ),
                    SearchOptionView.OptionItem(
                        name = i18n(CommonString.detect_model_gal1),
                        value = "game_model_kirakira"
                    )
                )
            )
        )

    override fun onImageSelected(imageUri: Uri, selectedOptions: List<SearchOptionView.Option>) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onDetectCharacterLiveData.value = null
            },
            block = {
                val selectModel: SearchOptionView.OptionItem =
                    (if (selectedOptions.isNotEmpty()) selectedOptions.first().selected else searchOptions.first().options?.first())
                        ?: throw IllegalArgumentException(i18n(CommonString.anime_character_select_model))

                onDetectCharacterLiveData.value = withContext(Dispatchers.IO) {
                    val targetFile = FileUtils.getFileByPath(ImageHelper.compressImage(imageUri))
                    val requestBody = targetFile.asRequestBody("image/*".toMediaType())
                    val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
                    val entities = BgmApiManager.bgmJsonApi.queryAnimeCharacter(
                        model = selectModel.value,
                        forceOne = 1,
                        aiDetect = 0,
                        file = body
                    ).data.orEmpty().onEach {
                        it.imageFile = cropImage(targetFile, it.box.orEmpty(), it.boxId.orEmpty())
                    }

                    if (entities.isEmpty()) {
                        throw IllegalArgumentException(i18n(CommonString.detect_failed))
                    }

                    ListParcelableWrapEntity(entities)
                }
            }
        )
    }

    private suspend fun cropImage(imageFile: File, box: List<Double>, boxId: String): File? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val bitmap = imageProcessor.cropImage(imageFile, box)
                val targetImagePath = PathUtils.getFilesPathExternalFirst() + "/image/$boxId.png"
                FileUtils.createFileByDeleteOldFile(targetImagePath)
                ImageUtils.save(bitmap, targetImagePath, Bitmap.CompressFormat.PNG, true)
                FileUtils.getFileByPath(targetImagePath)
            }.getOrNull()
        }
    }
}