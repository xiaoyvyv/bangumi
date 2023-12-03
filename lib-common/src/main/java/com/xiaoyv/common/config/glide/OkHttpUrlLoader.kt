package com.xiaoyv.common.config.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

/**
 * 一个简单的模型加载器，用于使用 OkHttp 通过 http/https 获取媒体。
 */
class OkHttpUrlLoader(private val client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {
    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(
        model: GlideUrl, width: Int, height: Int,
        options: Options
    ): LoadData<InputStream> {
        return LoadData(model, OkHttpStreamFetcher(client, model))
    }

    /**
     * [OkHttpUrlLoader] 的默认工厂。
     *
     * 使用静态单例客户端运行请求的新 Factory 的构造函数。
     */
    class Factory(private val client: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {

        /**
         * 使用给定客户端运行请求的新 Factory 的构造函数。
         *
         * @param客户端，这通常是“OkHttpClient”的实例。
         */
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}