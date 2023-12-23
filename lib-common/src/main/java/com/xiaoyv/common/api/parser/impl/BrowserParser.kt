package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.groupValue
import com.xiaoyv.common.kts.groupValueOne
import org.jsoup.nodes.Element

/**
 * Class: [BrowserParser]
 *
 * @author why
 * @since 11/25/23
 */
object BrowserParser {

    /**
     * 解析条目列表
     */
    fun Element.parserBrowserPage(@MediaType mediaType: String? = null): BrowserEntity {
        requireNoError()

        val browserEntity = BrowserEntity()

        browserEntity.items = select("#browserItemList > li").map { item ->
            val entity = BrowserEntity.Item()

            entity.id = item.select("a.cover").hrefId()
            entity.coverImage = item.select("a.cover img").attr("src").optImageUrl()
            entity.title = item.select(".inner h3 a").text()
            entity.subtitle = item.select(".inner h3 small").text()
            entity.rank = item.select(".rank").text().replace("Rank\\s+".toRegex(), "No.")
            entity.infoTip = parserInfoTip(item.select(".info.tip").text())

            val rateInfo = item.select(".rateInfo")
            entity.rating = rateInfo.parseStar()
            entity.ratingScore = rateInfo.select(".fade").text()
            entity.ratingCount = rateInfo.select(".tip_j").text()
            entity.collectTime = item.select(".collectInfo .tip_j").text()
            entity.tags = item.select(".collectInfo .tip").text()

            val collectBlock = item.select(".collectBlock")
            entity.isCollection = collectBlock.select(".collectModify").isNotEmpty()
            entity.mediaType = mediaType.orEmpty()
            entity.mediaTypeName = GlobalConfig.mediaTypeName(entity.mediaType)
            entity
        }

        return browserEntity
    }

    private val infoTimeRegex by lazy { "(\\d{4}-\\d{1,2})|((\\d{4})年(\\d{1,2})月)".toRegex() }
    private val infoEpsRegex by lazy { "(\\d+\\s*话)".toRegex() }

    /**
     * 解析条目类列表的详细描述信息
     *
     * - 搜索列表
     * - 全部浏览列表
     * - 收藏列表
     *  25话 / 2023年10月1日 / 窪岡俊之 / 硬梨菜・不二涼介（講談社「週刊少年マガジン」連載） / 倉島亜由美
     */
    fun parserInfoTip(infoTip: String): BrowserEntity.InfoTip {
        val time = infoTimeRegex.groupValue(infoTip, 0)
        val eps = infoEpsRegex.groupValueOne(infoTip)
        val info = infoTip
            .replace(infoEpsRegex, "")
            .split("/")
            .filter { it.isNotBlank() }
            .joinToString(" / ") { it.trim() }

        return BrowserEntity.InfoTip(time = time, eps = eps, info = info)
    }
}