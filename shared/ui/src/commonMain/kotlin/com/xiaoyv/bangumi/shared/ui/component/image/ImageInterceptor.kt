package com.xiaoyv.bangumi.shared.ui.component.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import coil3.request.ImageResult
import com.xiaoyv.bangumi.shared.core.utils.blankImageUrlRegex
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath

object ImageInterceptor : Interceptor {
    const val DEFAULT_IMAGE = "https://lain.bgm.tv/pic/photo/l/47/7e/837364_B9CUJ.jpg"
    private const val UA =
        "api-client/1 com.douban.frodo/7.65.0(277) Android/33 product/coral vendor/Google model/Pixel 4 XL brand/google  rom/android  network/wifi  udid/0643fa6abfd3eaff076ff3ee603211ded11fc344  platform/mobile nd/1"

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        if (data !is String) return chain.proceed()

        // 豆瓣数据图片特殊处理
        if (data.contains("douban")) {
            return chain.withRequest(
                chain.request
                    .newBuilder()
                    .httpHeaders(
                        chain.request.httpHeaders.newBuilder()
                            .set(HttpHeaders.Referrer, data.encodeURLPath())
                            .set(HttpHeaders.Accept, ContentType.Image.Any.toString())
                            .set(HttpHeaders.UserAgent, UA)
                            .build()
                    )
                    .build()
            ).proceed()
        }

        // 替换默认BGM图片加载的分辨率
        val url = if (blankImageUrlRegex.matches(data)) DEFAULT_IMAGE
        else if (data.contains("/lain.bgm.tv/r/")) data
        else data.replace("/pic/cover/l/", "/r/800/pic/cover/l/")

        return chain.withRequest(
            chain.request
                .newBuilder()
                .data(url)
                .build()
        ).proceed()
    }
}