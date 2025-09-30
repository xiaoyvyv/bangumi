package com.xiaoyv.bangumi.shared.data.api.client.converter

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.xiaoyv.bangumi.shared.core.exception.ApiException
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

/**
 * [HttpDocumentConverterFactory]
 *
 * @author why
 * @since 2025/1/14
 */
class HttpDocumentConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Document::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Failure -> throw ApiException(cause = result.throwable)
                        is KtorfitResult.Success -> result.response.bodyAsText().let {
                            Ksoup.parse(it, ktorfit.baseUrl)
                        }
                    }
                }
            }
        }
        return super.suspendResponseConverter(typeData, ktorfit)
    }
}