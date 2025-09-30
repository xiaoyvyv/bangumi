package com.xiaoyv.bangumi.shared.ui.component.image.cropper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.CropperLoading
import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.createResult
import com.attafitamim.krop.core.crop.cropState
import com.attafitamim.krop.core.crop.imageCropper
import com.attafitamim.krop.core.images.ImageSrc

/**
 * Creates an [imageCropper] instance.
 */
fun imageCropperCompat(): ImageCropperCompat = ImageCropperCompat()

class ImageCropperCompat : ImageCropper {
    override var cropState: CropState? by mutableStateOf(null)
    override var loadingStatus: CropperLoading? by mutableStateOf(null)

    suspend fun load(createSrc: suspend () -> ImageSrc?): Boolean {
        cropState = null
        val src = createSrc() ?: return false
        val newCrop = cropState(src)
        cropState = newCrop
        loadingStatus = CropperLoading.PreparingImage
        return true
    }

    suspend fun save(maxResultSize: IntSize? = null): CropResult {
        val state = cropState ?: return CropError.SavingError
        loadingStatus = CropperLoading.PreparingImage
        val result = state.createResult(maxResultSize)
        return if (result == null) CropError.SavingError
        else CropResult.Success(result)
    }

    override suspend fun crop(
        maxResultSize: IntSize?,
        createSrc: suspend () -> ImageSrc?,
    ): CropResult = CropResult.Cancelled
}