package com.xiaoyv.bangumi.shared.ui.component.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import com.xiaoyv.bangumi.shared.core.utils.blankImageUrlRegex
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath

object ImageInterceptor : Interceptor {
    const val DEFAULT_IMAGE = "https://lain.bgm.tv/pic/photo/l/47/7e/837364_B9CUJ.jpg"

    override suspend fun intercept(chain: Interceptor.Chain) = chain.withRequest(
        chain.request.newBuilder()
            .data(chain.request.data.let {
                if (it !is String) it else {
                    if (blankImageUrlRegex.matches(it)) DEFAULT_IMAGE else it
                }
            })
            .httpHeaders(
                chain.request.httpHeaders.newBuilder()
                    .set(HttpHeaders.Referrer, chain.request.data.toString().encodeURLPath())
                    .set(HttpHeaders.Accept, ContentType.Image.Any.toString())
                    .build()
            )
            .build()
    ).proceed()
}