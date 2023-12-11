package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.api.parser.entity.MagiRankEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.lastTextNode
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

    select(".magiQuiz").apply {
        entity.forms = select("input[type=hidden]")
            .map { it.attr("name") to it.attr("value") }
            .associate { it }

        // 有选项和无选项两种 UI 解析
        if (select(".opts > li input").isNotEmpty()) {
            entity.options = select(".opts > li").map { item ->
                val answerInput = item.select("input")
                val label = item.select("label").text()
                MagiQuestionEntity.Option(
                    id = answerInput.attr("value"),
                    field = answerInput.attr("name"),
                    label = label
                )
            }
        } else {
            entity.options = select(".opts > li").map { item ->
                item.select("span").remove()
                MagiQuestionEntity.Option(
                    id = item.text(),
                    field = "",
                    label = item.text(),
                    error = item.hasClass("wrong"),
                    right = item.hasClass("answer"),
                )
            }
        }

        select(".quizInfo").apply {
            entity.userId = select("a.avatar").hrefId()
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
        entity.lastQuestionCount = select(".latestQuiz > small.grey").lastTextNode()

        entity.lastQuestionOptions = select(".opts > li").map { item ->
            item.select("span").remove()
            MagiQuestionEntity.Option(
                id = item.text(),
                field = "",
                label = item.text(),
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

/**
 * 同步率排行
 */
fun Element.parserMagiRank(): MagiRankEntity {
    requireNoError()

    val entity = MagiRankEntity()
    val listList = select(".magiRankColumn").map { page ->
        page.select(".magiRank > li").map { item ->
            val rank = MagiRankEntity.MagiRank()
            rank.id = item.select("a.avatar").hrefId()
            rank.userName = item.select(".inner .title").text()
            rank.userAvatar = item.select("img").attr("src").optImageUrl()
            rank.correct = item.select(".correctBar").text().parseCount()
            rank.answered = item.select(".answeredBar").text().parseCount()
            rank
        }
    }

    entity.rateRank = listList.getOrNull(0).orEmpty()
    val rateCorrectMaxCount = entity.rateRank.maxOf { it.correct }
    val rateAnsweredMaxCount = entity.rateRank.maxOf { it.answered }
    entity.rateRank.onEach {
        it.correctMaxCount = rateCorrectMaxCount
        it.answeredMaxCount = rateAnsweredMaxCount
    }

    entity.createRank = listList.getOrNull(1).orEmpty()
    val createCorrectMaxCount = entity.createRank.maxOf { it.correct }
    val createAnsweredMaxCount = entity.createRank.maxOf { it.answered }
    entity.createRank.onEach {
        it.correctMaxCount = createCorrectMaxCount
        it.answeredMaxCount = createAnsweredMaxCount
    }

    return entity
}

/**
 * 历史查询
 */
fun Element.parserMagiHistory(): List<MagiQuestionEntity> {
    requireNoError()

    return select(".quizList > li").map { item ->
        val entity = MagiQuestionEntity()
        entity.lastQuestionRight = !item.hasClass("wrong")
        entity.lastQuestionId = item.select("h3 a").hrefId()
        entity.lastQuestionTitle = item.select("h3 a").text()
        entity.lastQuestionRightRate = item.select("small.na").text()
        entity.lastQuestionCount = item.select("small.grey").lastTextNode()
        entity
    }
}
