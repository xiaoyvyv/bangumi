package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.isIpv4Address
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUploadImage
import com.xiaoyv.bangumi.shared.data.model.response.chore.CloudflareDnsResponse
import com.xiaoyv.bangumi.shared.data.model.response.trace.MicrosoftTranslate
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.sink
import io.github.vinceglb.filekit.source
import io.github.vinceglb.filekit.write
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.readAvailable
import kotlinx.io.buffered
import okio.ByteString.Companion.encodeUtf8

class ChoreRepositoryImpl(private val client: BgmApiClient) : ChoreRepository {

    override suspend fun fetchDns(hostname: String): Result<Pair<String, List<String>>> = runResult {
        val normalizedHostname = hostname.trim().lowercase().removeSuffix(".")
        require(normalizedHostname.isNotBlank()) { "Hostname cannot be blank" }

        val endpoints = listOf(WebConstant.CLOUDFLARE_DNS_ENDPOINT_1, WebConstant.CLOUDFLARE_DNS_ENDPOINT_2)
        var lastException: Throwable? = null

        for (endpoint in endpoints) {
            try {
                val httpResponse = client.dnsHttpClient.get(endpoint) {
                    parameter("name", normalizedHostname)
                    parameter("type", "A")
                    header(HttpHeaders.Accept, "application/dns-json")
                }
                if (httpResponse.status.value in 200..299) {
                    val response = defaultJson.decodeFromString<CloudflareDnsResponse>(httpResponse.bodyAsText())
                    if (response.status == 0) {
                        val addresses = response.answers
                            .asSequence()
                            .filter { it.type == 1 }
                            .map { it.data.trim() }
                            .filter { it.isIpv4Address() }
                            .distinct()
                            .toList()
                        if (addresses.isNotEmpty()) {
                            return@runResult normalizedHostname to addresses
                        }
                    }
                }
            } catch (e: Throwable) {
                lastException = e
            }
        }
        throw lastException ?: IllegalStateException("No IPv4 address found for $normalizedHostname via DoH")
    }

    override suspend fun fetchMediaPictureByUrl(url: String): Result<PlatformFile> = runResult {
        val response = client.imageHttpClient.get(url)
        require(response.status.value in 200..299) {
            "Failed to download image: HTTP ${response.status.value}"
        }

        val contentType = response.headers[HttpHeaders.ContentType].orEmpty().lowercase()
        val extension = when {
            contentType.contains("jpeg") || contentType.contains("jpg") -> "jpg"
            contentType.contains("png") -> "png"
            contentType.contains("gif") -> "gif"
            contentType.contains("webp") -> "webp"
            else -> url.substringAfterLast(".", "jpg").substringBefore("?").ifBlank { "jpg" }
        }

        val saveDir = FileKit.cacheDir.div("download_image").apply { createDirectories() }
        val fileName = "${url.encodeUtf8().md5().hex()}.$extension"
        val destFile = saveDir.div(fileName)

        val channel = response.bodyAsChannel()
        val sink = destFile.sink().buffered()
        try {
            val buffer = ByteArray(64 * 1024)
            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                if (bytesRead <= 0) break
                sink.write(buffer, 0, bytesRead)
            }
            sink.flush()
        } finally {
            sink.close()
        }

        destFile
    }

    override suspend fun compressImageAndUpload(file: PlatformFile): Result<ComposeUploadImage> =
        runResult {
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

    override suspend fun translate(text: String, isHtml: Boolean): Result<String> =
        client.requestTraceApi {
            val responseString = submitMicrosoftTranslate(
                textType = if (isHtml) "html" else "plain",
                param = listOf(text)
            )
            defaultJson.decodeFromString<List<MicrosoftTranslate>>(responseString).joinToString("\n") {
                it.translations.joinToString(", ") { translation ->
                    translation.text
                }
            }
        }

}
