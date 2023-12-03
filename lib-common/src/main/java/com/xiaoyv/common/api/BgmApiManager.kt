package com.xiaoyv.common.api

import com.blankj.utilcode.util.Utils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.xiaoyv.common.api.api.BgmJsonApi
import com.xiaoyv.common.api.api.BgmWebApi
import com.xiaoyv.common.api.converter.WebDocumentConverter
import com.xiaoyv.common.api.converter.WebHtmlConverter
import com.xiaoyv.common.api.interceptor.CommonInterceptor
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Class: [BgmApiManager]
 *
 * @author why
 * @since 11/18/23
 */
class BgmApiManager {

    private val cookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(Utils.getApp()))
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(CommonInterceptor())
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .cookieJar(cookieJar)
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val noCookieHttpClient by lazy {
        httpClient.newBuilder()
            .cookieJar(CookieJar.NO_COOKIES)
            .build()
    }

    /**
     * 此 Retrofit 有 Cookie 持久化
     */
    private val webRetrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(WebHtmlConverter.create())
            .addConverterFactory(WebDocumentConverter.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .baseUrl(URL_BASE_WEB)
            .build()
    }

    /**
     * 此 Retrofit 没有 Cookie 持久化
     */
    private val apiRetrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(WebHtmlConverter.create())
            .addConverterFactory(WebDocumentConverter.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(noCookieHttpClient)
            .baseUrl(URL_BASE_API)
            .build()
    }

    private val bgmWebApi by lazy {
        webRetrofit.create(BgmWebApi::class.java)
    }

    private val bgmJsonApi by lazy {
        apiRetrofit.create(BgmJsonApi::class.java)
    }

    /**
     * 清空 Cookie
     */
    private fun resetCookie() {
        cookieJar.clear()
    }

    companion object {
        const val URL_BASE_WEB = "https://bangumi.tv"
        const val URL_BASE_API = "https://api.bgm.tv"

        private val instance by lazy { BgmApiManager() }

        val bgmJsonApi: BgmJsonApi
            get() = instance.bgmJsonApi

        val bgmWebApi: BgmWebApi
            get() = instance.bgmWebApi

        val httpClient
            get() = instance.httpClient

        fun resetCookie() {
            instance.resetCookie()
        }
    }
}