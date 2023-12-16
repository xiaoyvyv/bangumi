@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserSearchResult
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.CommonString
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import kotlin.random.Random

/**
 * Class: [SearchDetailViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SearchDetailViewModel : BaseListViewModel<SearchResultEntity>() {
    internal val currentSearchItem = MutableLiveData<SearchItem?>()

    /**
     * 是否为媒体搜索选取模式
     */
    internal val forSelectedMedia: Boolean
        get() = currentSearchItem.value?.forSelectedMedia == true

    internal val isLegacy = MutableLiveData(1)

    /**
     * 是否搜索标签
     */
    internal val isSearchTag
        get() = currentSearchItem.value?.pathType == BgmPathType.TYPE_SEARCH_TAG

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        val searchItem = requireNotNull(currentSearchItem.value)

        // 填充上次搜索时间的 Cookie
        val fakeTime = System.currentTimeMillis() / 1000 - Random.nextInt(10000)
        val fakeCookie = "chii_searchDateLine=$fakeTime;"
        val httpUrl = BgmApiManager.URL_BASE_WEB.toHttpUrl()
        val cookie = Cookie.parse(httpUrl, fakeCookie)
        if (cookie != null) {
            val newCookie = cookie.newBuilder()
                .expiresAt(System.currentTimeMillis() + TimeConstants.DAY)
                .build()
            BgmApiManager.httpClient.cookieJar.saveFromResponse(httpUrl, listOf(newCookie))
        }

        // 搜索逻辑
        when (searchItem.pathType) {
            // 条目、人物 搜索
            BgmPathType.TYPE_SEARCH_SUBJECT, BgmPathType.TYPE_SEARCH_MONO -> {
                return BgmApiManager.bgmWebApi.querySearchMedia(
                    pathType = searchItem.pathType,
                    keyword = searchItem.keyword,
                    cat = searchItem.id,
                    page = current,
                    legacy = isLegacy.value ?: 0
                ).parserSearchResult(searchItem.pathType).apply {
                    if (isRefresh && isEmpty()) {
                        throw IllegalArgumentException(StringUtils.getString(CommonString.common_empty_tip))
                    }
                }
            }

            // 标签搜索
            BgmPathType.TYPE_SEARCH_TAG -> {
                return BgmApiManager.bgmWebApi.querySearchTag(
                    mediaType = searchItem.id,
                    keyword = searchItem.keyword,
                ).parserSearchResult(searchItem.pathType).apply {
                    if (isRefresh && isEmpty()) {
                        throw IllegalArgumentException(StringUtils.getString(CommonString.common_empty_tip))
                    }
                }
            }

            else -> throw IllegalArgumentException("不支持的搜索类型：${searchItem.pathType}")
        }
    }
}