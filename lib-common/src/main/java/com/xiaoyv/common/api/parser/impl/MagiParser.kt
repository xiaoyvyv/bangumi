package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.requireNoError
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/11/23
 */
fun Element.parserMagiQuestion(): MagiQuestionEntity {
    requireNoError()

    val entity = MagiQuestionEntity()

    select("#columnAppA").apply {
        val h2 = select(".magiQuiz h2")
        entity.id = h2.select("small").remove().text().parseCount().toString()
        entity.title = h2.text()
    }

    select("#answer_quiz").apply {
        entity.forms = select("input[type=hidden]")
            .map { it.attr("name") to it.attr("value") }
            .associate { it }

        entity.options = select(".opts > li").map { item ->
            val answerInput = item.select("input")
            val label = item.select("label").text()
            MagiQuestionEntity.Option(
                id = answerInput.attr("value"),
                field = answerInput.attr("name"),
                label = label
            )
        }

        select(".quizInfo").apply {
            entity.userId = select("a.avatar").attr("href").substringAfterLast("/")
            entity.userAvatar = select("a.avatar img").attr("src").optImageUrl()
            entity.userName = select(".inner .title").text()
        }
    }

    select("#columnAppB").apply {
        val h3 = select(".latestQuiz h3")
        h3.select("p").remove()
        entity.lastQuestionTitle = h3.text()
        entity.lastQuestionId = select(".latestQuiz > small.grey a").text().parseCount().toString()
        entity.lastQuestionRightRate = select(".latestQuiz small.na").text()
        entity.lastQuestionCount =
            select(".latestQuiz > small.grey").textNodes().lastOrNull()?.text().orEmpty()

        entity.lastQuestionOptions = select(".opts > li").map { item ->
            val answerInput = item.select("input")
            val label = item.select("label").text()
            MagiQuestionEntity.Option(
                id = answerInput.attr("value"),
                field = answerInput.attr("name"),
                label = label,
                error = item.hasClass("wrong"),
                right = item.hasClass("answer"),
            )
        }
    }

    entity.syncRate = select(".magiHeader small.na").text()
    entity.syncCount = select(".magiHeader small.grey").textNodes()
        .lastOrNull()?.text().orEmpty()

    return entity
}