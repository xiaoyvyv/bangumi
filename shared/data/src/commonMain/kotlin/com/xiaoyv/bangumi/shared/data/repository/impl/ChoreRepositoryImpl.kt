package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUploadImage
import com.xiaoyv.bangumi.shared.data.model.response.trace.MicrosoftTranslateParam
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.source
import io.github.vinceglb.filekit.write
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.io.buffered

class ChoreRepositoryImpl(private val client: BgmApiClient) : ChoreRepository {
    private var cacheTranslateToken = ""

    override suspend fun compressImageAndUpload(file: PlatformFile): Result<ComposeUploadImage> = runResult {
        val compressFile = compressImage(file).getOrThrow()
        uploadImage(compressFile).getOrThrow()
    }

    override suspend fun compressImage(file: PlatformFile): Result<PlatformFile> {
        val compressedBytes = FileKit.compressImage(
            bytes = file.readBytes(),
            quality = 90,
            maxWidth = 1024,
            maxHeight = 1024,
            imageFormat = ImageFormat.JPEG
        )

        // Save the compressed image
        val compressedFile = PlatformFile(FileKit.filesDir, "compressed.jpg")
        compressedFile.write(compressedBytes)
        return Result.success(compressedFile)
    }

    override suspend fun uploadImage(file: PlatformFile): Result<ComposeUploadImage> = runResult {
        val provider = InputProvider { file.source().buffered() }
        val multipart = MultiPartFormDataContent(formData {
            append("file", provider, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"blog_image.jpg\"")
            })
        })
        client.bgmWebApi.submitUploadImage(multipart)
    }

    override suspend fun translate(text: String, isHtml: Boolean): Result<String> = client.requestTraceApi {
        val microsoftToken = cacheTranslateToken.ifBlank {
            queryEdgeAuthToken().bodyAsText().apply {
                cacheTranslateToken = this
            }
        }

        submitMicrosoftTranslate(
            authentication = "Bearer $microsoftToken",
            param = listOf(MicrosoftTranslateParam(text = text))
        ).joinToString("\n") {
            it.translations.joinToString(", ") { translation ->
                translation.text
            }
        }
    }
}