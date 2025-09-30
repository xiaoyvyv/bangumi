package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUploadImage
import io.github.vinceglb.filekit.PlatformFile

interface ChoreRepository {
    suspend fun compressImageAndUpload(file: PlatformFile): Result<ComposeUploadImage>

    suspend fun compressImage(file: PlatformFile): Result<PlatformFile>

    suspend fun uploadImage(file: PlatformFile): Result<ComposeUploadImage>

    suspend fun translate(text: String, isHtml: Boolean): Result<String>
}
