@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.StringUtils
import com.huaban.analysis.jieba.JiebaSegmenter
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserSearchResult
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
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
    private val wordSegment by lazy { JiebaSegmenter() }

    /**
     * 是否为媒体搜索选取模式
     */
    internal val forSelectedMedia: Boolean
        get() = currentSearchItem.value?.forSelectedMedia == true

    internal val isLegacy = MutableLiveData(1)

    /**
     * 搜索类型
     */
    internal val searchBgmType: String
        get() = currentSearchItem.value?.pathType.orEmpty()

    /**
     * 关键词
     */
    internal val keywords: MutableList<String> = mutableListOf()

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
            // 小组话题帖子搜索
            BgmPathType.TYPE_TOPIC -> {
                segmentWords(searchItem.keyword)
                return BgmApiManager.bgmJsonApi.querySearchTopic(
                    keyword = searchItem.keyword,
                    exact = isLegacy.value == 0,
                    current = current
                ).data?.records.orEmpty().map {
                    SearchResultEntity(id = it.id.toString(), payload = it)
                }
            }
            // 小组话题目录搜索
            BgmPathType.TYPE_INDEX -> {
                segmentWords(searchItem.keyword)
                return BgmApiManager.bgmJsonApi.querySearchIndex(
                    keyword = searchItem.keyword,
                    exact = isLegacy.value == 0,
                    current = current
                ).data?.records.orEmpty().map {
                    SearchResultEntity(id = it.id.toString(), payload = it)
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

    private suspend fun segmentWords(keyword: String) {
        return withContext(Dispatchers.IO) {
            val time = System.currentTimeMillis()
            val segmentWords = wordSegment.process(keyword, JiebaSegmenter.SegMode.INDEX)
                .map { it.word.orEmpty() }

            keywords.clear()
            keywords.addAll(segmentWords)
            debugLog { "耗时：${System.currentTimeMillis() - time}" }
        }
    }

    fun refreshKeyword(keyword: String) {
        currentSearchItem.value?.keyword = keyword
    }
}