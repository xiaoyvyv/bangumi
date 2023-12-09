@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserSearchResult
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.CommonString
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * Class: [SearchDetailViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SearchDetailViewModel : BaseListViewModel<SearchResultEntity>() {
    internal val currentSearchItem = MutableLiveData<SearchItem?>()

    internal val isLegacy = MutableLiveData(1)

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        val searchItem = requireNotNull(currentSearchItem.value)

        // 填充上次搜索时间的 Cookie
        val fakeCookie = "chii_searchDateLine=0;"
        val httpUrl = BgmApiManager.URL_BASE_WEB.toHttpUrl()
        val cookie = Cookie.parse(httpUrl, fakeCookie)
        if (cookie != null) {
            BgmApiManager.httpClient.cookieJar.saveFromResponse(httpUrl, listOf(cookie))
        }

        return BgmApiManager.bgmWebApi.querySearch(
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
}