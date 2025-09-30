package com.xiaoyv.bangumi.shared.data.api.client.converter

import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

internal class HttpCodeConverterFactory : Converter.Factory {
    class BgmHttpCodeConverter(
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
                        throw ApiHttpException(result.response.status.value, result.response.bodyAsText())
                    }
                }
            }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, Any?>? {
        if (typeData.typeInfo.type != Response::class) {
            return BgmHttpCodeConverter(typeData, ktorfit)
        }
        return null
    }
}
