package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.api.parser.optImageUrl
import org.jsoup.nodes.Document

/**
 * Class: [BrowserParser]
 *
 * @author why
 * @since 11/25/23
 */
object BrowserParser {

    fun Document.parserBrowserPage(): BrowserEntity {
        val browserEntity = BrowserEntity()

        browserEntity.items = select("#browserItemList > li").map {
            val item = BrowserEntity.Item()

            item.subjectId = it.select("a.cover").attr("href").substringAfterLast("/")
            item.coverImage = it.select("a.cover img").attr("src").optImageUrl()
            item.titleCn = it.select(".inner h3 a").text()
            item.title = it.select(".inner h3 small").text()
            item.rank = it.select(".rank").text()
            item.infoTip = parserInfoTip(it.select(".tip").text())

            val rateInfo = it.select(".rateInfo")
            item.rating = rateInfo.select(".starstop-s > span").attr("class")
            item.ratingScore = rateInfo.select(".fade").text()
            item.ratingCount = rateInfo.select(".tip_j").text()

            val collectBlock = it.select(".collectBlock")
            item.isCollection = collectBlock.select(".collectModify").isNotEmpty()

            item
        }

        val groupMenu = select("#columnSubjectBrowserB .sideInner").firstOrNull()
        if (groupMenu != null) {
            val options = arrayListOf<BrowserEntity.Option>()
            groupMenu.children().forEach {
                if (it.hasClass("subtitle")) {
                    val option = BrowserEntity.Option()
                    option.title = it.text()

                    val sibling = it.nextElementSibling()
                    if (sibling != null && sibling.hasClass("grouped")) {
                        option.items = sibling.select("li > a").map { a ->
                            val path = a.attr("href")
                            val name = a.text()
                            BrowserEntity.OptionItem(path, name)
                        }
                    }
                    options.add(option)
                }
            }
            browserEntity.options = options
        }
        return browserEntity
    }

    private fun parserInfoTip(infoTip: String): BrowserEntity.InfoTip {
        val regex = "(\\d{4}-\\d{1,2})|((\\d{4})年(\\d{1,2})月)".toRegex()
        val time = regex.find(infoTip)?.groupValues?.firstOrNull().orEmpty()
        val eps = "\\d\\s+话".toRegex().find(infoTip)?.value.orEmpty()
        return BrowserEntity.InfoTip(time = time, eps = eps)
    }
}