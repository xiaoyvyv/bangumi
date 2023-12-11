package com.xiaoyv.common

import com.xiaoyv.common.api.SignHelper
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object DoubanWebRequest {
    private const val UA_DOUBAN_ANDROID =
        "api-client/1 com.douban.frodo/7.65.0(277) Android/33 product/coral vendor/Google model/Pixel 4 XL brand/google  rom/android  network/wifi  udid/0643fa6abfd3eaff076ff3ee603211ded11fc344  platform/mobile nd/1"

    fun downloadWebSiteUseGet(baseLink: String, paramsMap: MutableMap<String, String>): String {
        val httpUrlBuilder = baseLink.toHttpUrl().newBuilder()

        paramsMap["apikey"] = "0dad551ec0f84ed02907ff5c42e8ec70"
        paramsMap.forEach { (name, value) ->
            httpUrlBuilder.addQueryParameter(name, value)
        }
        val pair = SignHelper.pair(httpUrlBuilder.toString(), "GET", "")
        val signPair = requireNotNull(pair)

        httpUrlBuilder.addQueryParameter("_sig", signPair.first)
        httpUrlBuilder.addQueryParameter("_ts", signPair.second)

        val httpClient = OkHttpClient()
        val httpUrl = httpUrlBuilder.build()

        val builder = Request.Builder()
            .url(httpUrl)
            .addHeader("User-Agent", UA_DOUBAN_ANDROID)

        System.err.println("Url: $httpUrl")
        System.err.println("User-Agent: $UA_DOUBAN_ANDROID")

        return httpClient.newCall(builder.build()).execute().body.string()
    }
}