package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/9/23
 */
fun Element.parserMessageList(boxType: String): Pair<String, List<MessageEntity>> {
    requireNoError()

    val gh = select("#pmForm").attr("action").substringAfterLast("=")
    return gh to select("#pmForm table > tbody > tr")
        .filter { item -> item.select(".erase").isNotEmpty() }
        .map { item ->
            val entity = MessageEntity()
            entity.boxType = boxType
            entity.field = item.select("input").attr("name")
            entity.id = item.select("input").attr("value")
            entity.fromAvatar = item.select("img.avatar").attr("src").optImageUrl()
            entity.fromId = item.select(".sub_title a").attr("href").substringAfterLast("/")
            entity.fromName = item.select(".sub_title a").text()
            entity.time = item.select("small.grey").text()
            entity.summary = item.select(".tip").html().parseHtml()
            entity.isRead = item.select(".pm_new").isNotEmpty()
            entity
        }
}

/**
 * 解析消息聊天列表
 */
fun Element.parserMessageBox(messageId: String): List<MessageEntity> {
    requireNoError()

    return select("#comment_box > .item").map { item ->
        val entity = MessageEntity()
        entity.id = messageId
        entity.fromAvatar = item.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        entity.fromId = item.select("a.avatar").attr("href").substringAfterLast("/")
        entity.isMine = item.select(".text_main_odd").isNotEmpty()
        entity.mineAvatar = UserHelper.currentUser.avatar?.large.orEmpty()

        // 内容部分
        val textPm = item.select(".text_pm")
        textPm.select(".rr").remove().apply {
            entity.time = select("small.grey").text().substringBefore("/").trim()
            entity.deleteHash = select("small.grey a").attr("onclick").let {
                "erasePM\\('[\\s\\S]+','(.*?)'\\)".toRegex()
                    .find(it)?.groupValues?.getOrNull(1).orEmpty()
            }
        }
        useNotNull(textPm.select("a.l").firstOrNull()) {
            entity.fromName = text()
            remove()
        }

        // 内容主题
        val pmBoard = textPm.select(".board").firstOrNull()
        if (pmBoard != null) {
            val subject = pmBoard.previousElementSibling()
            if (subject != null) {
                entity.subject = subject.text()
                subject.remove()
            }
        }

        // 内容的详细信息
        entity.summary = textPm.html()
            .trim().removePrefix(":")
            .trim().parseHtml()

        entity
    }
}

/**
 * 解析回复短信表单
 */
fun Element.parserMessageReplyForm(): MutableMap<String, String> {
    return select("#pmReplyForm input, #pmReplyForm textarea").map {
        val name = it.attr("name")
        val value = it.attr("value")
        name to value
    }.associate { it }.toMutableMap()
}