package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.MediaType
import org.jsoup.nodes.Document

/**
 * Class: [BrowserParser]
 *
 * @author why
 * @since 11/25/23
 */
object BrowserParser {

    /**
     * 是否是收藏的条目
     */
    fun Document.parserBrowserPage(
        @MediaType mediaType: String,
        isCollectList: Boolean = false
    ): BrowserEntity {
        val browserEntity = BrowserEntity()

        browserEntity.items = select("#browserItemList > li").map {
            val item = BrowserEntity.Item()

            item.subjectId = it.select("a.cover").attr("href").substringAfterLast("/")
            item.coverImage = it.select("a.cover img").attr("src").optImageUrl()
            item.title = it.select(".inner h3 a").text()
            item.subtitle = it.select(".inner h3 small").text()
            item.rank = it.select(".rank").text()
            item.infoTip = parserInfoTip(it.select(".tip").text())

            val rateInfo = it.select(".rateInfo")
            item.rating = rateInfo.select(".starstop-s > span").attr("class")
            item.ratingScore = rateInfo.select(".fade").text()

            if (isCollectList) {
                item.infoTip.time = rateInfo.select(".tip_j").text()
            } else {
                item.ratingCount = rateInfo.select(".tip_j").text()
            }

            val collectBlock = it.select(".collectBlock")
            item.isCollection = collectBlock.select(".collectModify").isNotEmpty()
            item.mediaType = mediaType
            item.mediaTypeName = GlobalConfig.mediaTypes.find { tab ->
                tab.type == mediaType
            }?.title.orEmpty()
            item
        }

        return browserEntity
    }

    private fun parserInfoTip(infoTip: String): BrowserEntity.InfoTip {
        val regex = "(\\d{4}-\\d{1,2})|((\\d{4})年(\\d{1,2})月)".toRegex()
        val time = regex.find(infoTip)?.groupValues?.firstOrNull().orEmpty()
        val eps = "\\d\\s+话".toRegex().find(infoTip)?.value.orEmpty()
        return BrowserEntity.InfoTip(time = time, eps = eps, fullTip = infoTip)
    }
}