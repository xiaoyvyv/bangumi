package com.xiaoyv.bangumi.shared.ui.component.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import coil3.request.ImageResult
import com.xiaoyv.bangumi.shared.core.utils.blankImageUrlRegex
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLPath
import org.koin.mp.KoinPlatform

object ImageInterceptor : Interceptor {
    const val DEFAULT_IMAGE = "https://lain.bgm.tv/pic/photo/l/47/7e/837364_B9CUJ.jpg"

    private const val DOU_BAN_UA = "api-client/1 com.douban.frodo/7.65.0(277) " +
            "Android/33 product/coral vendor/Google model/Pixel 4 XL brand/google  rom/android  network/wifi  " +
            "udid/0643fa6abfd3eaff076ff3ee603211ded11fc344  platform/mobile nd/1"

    private const val HOST_PIXIV_IMAGE = "i.pximg.net"

    private val preferenceStore: UserManager by lazy {
        KoinPlatform.getKoin().get()
    }

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
                            .set(HttpHeaders.UserAgent, DOU_BAN_UA)
                            .build()
                    )
                    .build()
            ).proceed()
        }

        // Pixiv image
        if (data.contains(HOST_PIXIV_IMAGE)) {
            val newUrl = preferenceStore.settings.network.pixivImageHost + data.substringAfter(HOST_PIXIV_IMAGE).trimStart('/')

            return chain.withRequest(
                chain.request
                    .newBuilder()
                    .httpHeaders(
                        chain.request.httpHeaders.newBuilder()
                            .set(HttpHeaders.Referrer, "https://www.pixiv.net/")
                            .set(HttpHeaders.Accept, ContentType.Image.Any.toString())
                            .build()
                    )
                    .data(newUrl)
                    .build()
            ).proceed()
        }

        // 替换 Bgm.TV 缺省图
        val url = if (blankImageUrlRegex.matches(data)) DEFAULT_IMAGE else data

        return chain.withRequest(
            chain.request
                .newBuilder()
                .data(url)
                .build()
        ).proceed()
    }
}