package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.image_detect_failed
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceName
import com.xiaoyv.bangumi.shared.data.repository.TraceRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString

/**
 * [TraceRepositoryImpl]
 *
 * @since 2025/5/15
 */
class TraceRepositoryImpl(
    private val client: BgmApiClient,
    private val json: Json,
) : TraceRepository {
    override suspend fun fetchAniTitleMapByEmbed(): Result<List<ComposeTraceName>> = runResult {
        val text = Res.readBytes("files/ani/title.json").decodeToString()
        json.decodeFromString<List<ComposeTraceName>>(text)
    }

    override suspend fun fetchAniTitleMapByJsdelivr(): Result<List<ComposeTraceName>> = runResult {
        client.mikanApi.fetchAniTitleMapByJsdelivr()
    }

    override suspend fun fetchAniTitleMapByGithub(): Result<List<ComposeTraceName>> = runResult {
        client.mikanApi.fetchAniTitleMapByGithub()
    }

    override suspend fun fetchSubjectInfoFromImage(byteArray: ByteArray): Result<ComposeTraceMoe> =
        runResult {
            client.traceApi.fetchSubjectInfoFromImage(
                name = ContentType.Image.JPEG.toString(),
                data = byteArray,
            ).also {
                require(!it.result.isEmpty()) {
                    getString(Res.string.image_detect_failed)
                }
            }
        }

    override suspend fun fetchCharacterInfoFromImage(
        byteArray: ByteArray,
        model: String,
        aiDetect: Boolean,
        showMulti: Boolean,
    ): Result<ComposeTraceCharacter> =
        runResult {
            val multipart = MultiPartFormDataContent(formData {
                append("model", model)
                append("ai_detect", aiDetect)
                append("is_multi", showMulti)
                append("file", byteArray, Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Image.PNG.toString())
                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpeg\"")
                })
            })
            client.traceApi.fetchCharacterInfoFromImage(body = multipart).also {
                require(!it.data.isNullOrEmpty()) {
                    getString(Res.string.image_detect_failed)
                }
            }
        }
}
