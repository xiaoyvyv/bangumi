package com.xiaoyv.bangumi.shared.data.api.client.converter

import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

internal class HttpCodeConverterFactory : Converter.Factory {
    class HttpCodeConverter(
        val typeData: TypeData,
        val ktorfit: Ktorfit,
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any =
            when (result) {
                is KtorfitResult.Failure -> throw result.throwable
                is KtorfitResult.Success -> {
                    if (result.response.status.value in (200 until 400)) {
                        result.response.call.body(typeData.typeInfo)
                    } else {
                        val text = result.response.bodyAsText().trim()
                        val isJson = result.response.headers[HttpHeaders.ContentType].orEmpty()
                        if (isJson.contains("json") && text.startsWith("{")) {
                            val info = defaultJson.decodeFromString<Map<String, JsonElement>>(text)
                            val errorMsg = info["message"] ?: info["msg"] ?: info["error"] ?: info["code"] ?: JsonPrimitive("")
                            throw ApiHttpException(
                                code = result.response.status.value,
                                errorMsg = if (errorMsg is JsonPrimitive) {
                                    errorMsg.jsonPrimitive.contentOrNull.orEmpty()
                                } else {
                                    errorMsg.toString()
                                }
                            )
                        } else {
                            throw ApiHttpException(result.response.status.value, text)
                        }
                    }
                }
            }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, Any?>? {
        if (typeData.typeInfo.type != Response::class) {
            return HttpCodeConverter(typeData, ktorfit)
        }
        return null
    }
}
