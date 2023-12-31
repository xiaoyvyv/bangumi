package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.UserDetailEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parserFormHash
import com.xiaoyv.common.api.parser.parserSignBackground
import com.xiaoyv.common.api.parser.preHandleHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.selectLegal
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.groupValueOne
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * @author why
 * @since 12/3/23
 */
fun Document.parserUserInfo(userId: String): UserDetailEntity {
    requireNoError()

    val entity = UserDetailEntity(id = userId)
    entity.gh = parserFormHash()

    // 解析 Int 类型好友ID
    entity.numberUid = select(".actions a.chiiBtn")
        .joinToString(" ") { it.attr("href") }
        .let { "(\\d{3,})".toRegex().groupValueOne(it) }

    selectLegal("#headerProfile").apply {
        entity.avatar = select(".headerAvatar span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        entity.nickname = select(".inner .name a").text()

        // 检测是否为好友
        entity.isFriend = select(".actions").html().contains("disconnectFriend", true)
    }

    select("#user_home").apply {
        entity.sign = select("blockquote .bio").html().preHandleHtml()
        entity.signPic = entity.sign.parserSignBackground()
        entity.networkService = select(".network_service > li").map { item ->
            val service = UserDetailEntity.NetworkService()
            service.title = item.select(".service").text()
            service.background = item.select(".service").attr("style")
                .substringAfter(":")
                .substringBefore(";")
            service.tip = item.select(".tip").html()
            service
        }
        entity.createTime = entity.networkService.firstOrNull()?.tip.orEmpty()
        entity.userSynchronize = select(".userSynchronize").let { item ->
            val synchronize = UserDetailEntity.Synchronize()
            synchronize.syncCount = item.select("small.hot").text().parseCount()
            synchronize.rate = item.select(".percent_text").text()
            synchronize
        }

        entity.anime = select("#anime").parserUserSaveOverview(MediaType.TYPE_ANIME)
        entity.book = select("#book").parserUserSaveOverview(MediaType.TYPE_BOOK)
        entity.music = select("#music").parserUserSaveOverview(MediaType.TYPE_MUSIC)
        entity.game = select("#game").parserUserSaveOverview(MediaType.TYPE_GAME)
        entity.real = select("#real").parserUserSaveOverview(MediaType.TYPE_REAL)
        entity.blog = select("#blog").firstOrNull()?.parserMediaBlog().orEmpty()
    }

    entity.lastOnlineTime = select("#pinnedLayout .timeline > li")
        .firstOrNull()?.select(".time")?.text().orEmpty()
        .replace("(\\d{4}-\\d{1,2}-\\d{1,2})\\s+\\d{1,2}:\\d{1,2}".toRegex(), "$1")

    entity.chart = select("#pinnedLayout .userStats").let { item ->
        val chart = UserDetailEntity.UserChart()
        val gridStats = item.select(".gridStats > .item")
        useNotNull(gridStats.getOrNull(0)) {
            chart.saveCount = select(".num").text().parseCount()
        }
        useNotNull(gridStats.getOrNull(1)) {
            chart.finishCount = select(".num").text().parseCount()
        }
        useNotNull(gridStats.getOrNull(2)) {
            chart.finishRate = select(".num").text().trim()
                .substringBefore("%").toFloatOrNull() ?: 0f
        }
        useNotNull(gridStats.getOrNull(3)) {
            chart.averageScore = select(".num").text().trim()
                .toFloatOrNull() ?: 0f
        }
        useNotNull(gridStats.getOrNull(4)) {
            chart.standardDeviation = select(".num").text().trim()
                .toFloatOrNull() ?: 0f
        }
        useNotNull(gridStats.getOrNull(5)) {
            chart.ratingCount = select(".num").text().parseCount()
        }
        chart.ratingDetail = item.select("#ChartWarpper .horizontalChart > li").map { rating ->
            val ratingItem = MediaDetailEntity.RatingItem()
            ratingItem.percent = rating.select("a.textTip").attr("title")
                .substringBefore("%").toFloatOrNull() ?: 0f
            ratingItem.label = rating.select(".label").text().parseCount()
            ratingItem.count = rating.select(".count").text().parseCount()
            ratingItem
        }
        chart
    }
    return entity
}


fun Elements.parserUserSaveOverview(@MediaType mediaType: String): UserDetailEntity.SaveOverview {
    val overview = UserDetailEntity.SaveOverview(isEmpty = true)
    val horizontalOptions = select(".horizontalOptions li")
    if (horizontalOptions.isEmpty()) return overview
    overview.isEmpty = false
    overview.title = horizontalOptions.select("li.title").remove().text()
    overview.count = horizontalOptions.map { it.text() }.toMutableList().apply { removeAt(0) }
    useNotNull(select("div.clearit .coversSmall").getOrNull(0)?.select("ul > li a")) {
        overview.doing = map { item ->
            val relative = MediaDetailEntity.MediaRelative()
            relative.titleCn = item.select(".name").text()
            relative.titleNative = item.select(".name").text()
            relative.id = item.hrefId()
            relative.cover = item.select("img").attr("src").optImageUrl(false)
            relative.collectType = InterestType.TYPE_DO
            relative.type = mediaType
            relative
        }
    }
    useNotNull(select("div.clearit .coversSmall").getOrNull(1)?.select("ul > li a")) {
        overview.collect = map { item ->
            val relative = MediaDetailEntity.MediaRelative()
            relative.titleCn = item.select(".name").text()
            relative.titleNative = item.select(".name").text()
            relative.id = item.hrefId()
            relative.cover = item.select("img").attr("src").optImageUrl(false)
            relative.collectType = InterestType.TYPE_COLLECT
            relative.type = mediaType
            relative
        }
    }
    return overview
}







