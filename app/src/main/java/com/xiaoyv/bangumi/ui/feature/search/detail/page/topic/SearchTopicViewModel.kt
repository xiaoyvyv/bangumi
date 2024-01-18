package com.xiaoyv.bangumi.ui.feature.search.detail.page.topic

import androidx.lifecycle.MutableLiveData
import com.huaban.analysis.jieba.JiebaSegmenter
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SearchTopicViewModel]
 *
 * @author why
 * @since 1/18/24
 */
class SearchTopicViewModel : BaseListViewModel<SearchResultEntity>() {
    internal var isLegacy = MutableLiveData(false)

    private val wordSegment by lazy { JiebaSegmenter() }

    /**
     * 关键词
     */
    internal var keyword = ""
    internal val keywords: MutableList<String> = mutableListOf()

    /**
     * 排序
     */
    internal var order: String? = null

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        require(keyword.isNotBlank()) { "请输入搜索内容" }

        segmentWords(keyword)
        return BgmApiManager.bgmJsonApi.querySearchTopic(
            keyword = keyword,
            exact = isLegacy.value == true,
            order = order,
            current = current
        ).data?.records.orEmpty().map {
            SearchResultEntity(id = it.id.toString(), payload = it)
        }
    }

    private suspend fun segmentWords(keyword: String) {
        return withContext(Dispatchers.IO) {
            val segmentWords = wordSegment.process(keyword, JiebaSegmenter.SegMode.INDEX)
                .map { it.word.orEmpty() }

            keywords.clear()
            keywords.addAll(segmentWords)
        }
    }
}