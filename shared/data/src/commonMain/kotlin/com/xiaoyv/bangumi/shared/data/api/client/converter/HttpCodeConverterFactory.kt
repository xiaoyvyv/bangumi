package com.xiaoyv.bangumi.shared.data.api.client.converter

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
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
                        try {
                            result.response.call.body(typeData.typeInfo)
                        } catch (throwable: Throwable) {
                            val text = result.response.bodyAsText().trim()
                            if (text.startsWith("<") || text.contains("<html")) {
                                val errorMsg = parseHtmlErrorMsg(text)
                                throw ApiHttpException(
                                    code = result.response.status.value,
                                    errorMsg = errorMsg
                                )
                            }
                            throw throwable
                        }
                    } else {
                        val text = result.response.bodyAsText().trim()
                        val isJson = result.response.headers[HttpHeaders.ContentType].orEmpty()
                        if (isJson.contains("json") && text.startsWith("{")) {
                            val info = defaultJson.decodeFromString<Map<String, JsonElement>>(text)
                            val errorMsg =
                                info["message"] ?: info["msg"] ?: info["error"] ?: info["code"]
                                ?: JsonPrimitive("")
                            throw ApiHttpException(
                                code = result.response.status.value,
                                errorMsg = if (errorMsg is JsonPrimitive) {
                                    errorMsg.jsonPrimitive.contentOrNull.orEmpty()
                                } else {
                                    errorMsg.toString()
                                }
                            )
                        } else {
                            val errorMsg = if (text.startsWith("<") || text.contains("<html")) {
                                parseHtmlErrorMsg(text)
                            } else {
                                text
                            }
                            throw ApiHttpException(
                                code = result.response.status.value,
                                errorMsg = errorMsg
                            )
                        }
                    }
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
            return HttpCodeConverter(typeData, ktorfit)
        }
        return null
    }
}
