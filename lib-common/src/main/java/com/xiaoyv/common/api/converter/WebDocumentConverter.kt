package com.xiaoyv.common.api.converter

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type


/**
 * Class: [WebDocumentConverter]
 *
 * @author why
 * @since 11/24/23
 */
class WebDocumentConverter : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == Document::class.java) ItemConverter else null
    }

    private object ItemConverter : Converter<ResponseBody, Document> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Document {
            return Jsoup.parse(value.use { it.string() })
        }
    }

    companion object {
        fun create(): WebDocumentConverter {
            return WebDocumentConverter()
        }
    }
}