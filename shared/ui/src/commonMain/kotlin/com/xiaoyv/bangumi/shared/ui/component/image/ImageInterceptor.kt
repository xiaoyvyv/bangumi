package com.xiaoyv.bangumi.shared.ui.component.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import coil3.request.ImageResult
import com.xiaoyv.bangumi.shared.core.utils.blankImageUrlRegex
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import org.koin.mp.KoinPlatform

object ImageInterceptor : Interceptor {
    private const val DOU_BAN_UA =
        "api-client/1 com.douban.frodo/7.133.0(361) Android/36  udid/1741fb3b9fbeccd7ab183ae025c88b0be11b41b5  douban_udid/25066f2ce5455eae4feec44dc5dfe1c3fb0fb873 model/25098PN5AC brand/Xiaomi  rom/miui6  network/wifi  platform/mobile  foldable/0 nd/1 product/pandora vendor/Xiaomi udid/1741fb3b9fbeccd7ab183ae025c88b0be11b41b5"

    private const val HOST_PIXIV_IMAGE = "i.pximg.net"

    private val preferenceStore: UserManager by lazy {
        KoinPlatform.getKoin().get()
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        if (data !is String) return chain.proceed()

        // 巡礼图片修复
        if (data.contains("image.anitabi.cn")) {
            return chain.withRequest(
                chain.request
                    .newBuilder()
                    .data(data.replace("image.anitabi.cn", "image-anitabi.magiconch.com"))
                    .build()
            ).proceed()
        }

        // 豆瓣数据图片特殊处理
        if (data.contains("douban")) {
            return chain.withRequest(
                chain.request
                    .newBuilder()
                    .httpHeaders(
                        chain.request.httpHeaders.newBuilder()
                            .set(HttpHeaders.Accept, ContentType.Image.Any.toString())
                            .set(HttpHeaders.UserAgent, DOU_BAN_UA)
                            .build()
                    )
                    .build()
            ).proceed()
        }

        // Pixiv image
        if (data.contains(HOST_PIXIV_IMAGE)) {
            val newUrl =
                preferenceStore.settings.network.pixivImageHost + data.substringAfter(HOST_PIXIV_IMAGE).trimStart('/')

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

        // 去除 Bgm.TV 缺省图
        val url = if (blankImageUrlRegex.matches(data)) "" else data

        return chain.withRequest(
            chain.request
                .newBuilder()
                .data(url)
                .build()
        ).proceed()
    }
}