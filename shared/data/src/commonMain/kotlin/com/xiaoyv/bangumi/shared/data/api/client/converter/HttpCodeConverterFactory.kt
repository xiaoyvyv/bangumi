package com.xiaoyv.bangumi.shared.data.api.client.converter

import com.fleeksoft.ksoup.Ksoup
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_too_many_request
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
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.jetbrains.compose.resources.getString

internal class HttpCodeConverterFactory : Converter.Factory {
    class HttpCodeConverter(
        private val typeData: TypeData,
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any =
            when (result) {
                is KtorfitResult.Failure -> throw result.throwable
                is KtorfitResult.Success -> {
                    val response = result.response
                    if (response.status.value in 200 until 400) {
                        handleSuccess(response)
                    } else {
                        handleError(response)
                    }
                }
            }

        private suspend fun handleSuccess(response: HttpResponse): Any {
            return try {
                response.call.body(typeData.typeInfo)
            } catch (throwable: Throwable) {
                val text = response.bodyAsText().trim()
                // 如果解析失败且内容看起来像 HTML，可能是返回了错误页面
                if (text.startsWith("<") || text.contains("<html")) {
                    throw ApiHttpException(
                        code = response.status.value,
                        errorMsg = parseHtmlErrorMsg(text)
                    )
                }
                throw throwable
            }
        }

        private suspend fun handleError(response: HttpResponse): Nothing {
            val text = response.bodyAsText().trim()
            val contentType = response.headers[HttpHeaders.ContentType].orEmpty()

            val errorMsg = when {
                response.status == HttpStatusCode.TooManyRequests -> {
                    getString(Res.string.global_too_many_request)
                }

                contentType.contains("json") && text.startsWith("{") -> {
                    parseJsonErrorMessage(text)
                }

                text.startsWith("<") || text.contains("<html") -> {
                    parseHtmlErrorMsg(text)
                }

                else -> text
            }

            throw ApiHttpException(
                code = response.status.value,
                errorMsg = errorMsg
            )
        }

        private fun parseJsonErrorMessage(jsonText: String): String {
            return try {
                val info = defaultJson.decodeFromString<Map<String, JsonElement>>(jsonText)
                val errorElement = info["message"] ?: info["msg"] ?: info["error"] ?: info["code"]
                when (errorElement) {
                    is JsonPrimitive -> errorElement.contentOrNull.orEmpty()
                    null -> ""
                    else -> errorElement.toString()
                }
            } catch (_: Throwable) {
                jsonText
            }
        }

        /**
         * 解析 HTML 页面，递归遍历并收集最底层的文本节点内容（自动跳过 script 与 style 标签）
         */
        private fun parseHtmlErrorMsg(html: String): String {
            return try {
                val doc = Ksoup.parse(html)
                doc.text()
            } catch (_: Throwable) {
                "Error"
            }
        }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, Any?>? {
        if (typeData.typeInfo.type != Response::class) {
            return HttpCodeConverter(typeData)
        }
        return null
    }
}
