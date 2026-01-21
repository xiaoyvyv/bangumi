@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePrivacy
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserStats
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toPersistentList

/**
 * [UserParser]
 *
 * @author why
 * @since 2025/1/16
 */
@AppParserDsl
class UserParser : BaseParser() {

    fun Element.fetchUserEditInfoConverted(): ComposeUserEdit {
        requireLogin()
        val items = select("table.settings > tbody > tr").map {
            it.select("tr td").let { elements ->
                elements.firsTextNode() to elements.getOrNull(1)
            }
        }
        return ComposeUserEdit(
            avatar = items.findValue("头像"),
            nickname = items.findValue("昵称"),
            sign = items.findValue("签名"),
            timezone = items.findValue("时区"),
            site = items.findValue("个人主页"),
            intro = items.findValue("自我介绍"),
            internetPsn = items.findValue("PSN"),
            internetXbox = items.findValue("Xbox Live"),
            internetSteam = items.findValue("Steam"),
            internetPixi = items.findValue("Pixiv"),
            internetGithub = items.findValue("GitHub"),
            internetTwitter = items.findValue("Twitter"),
            internetIns = items.findValue("Instagram"),
        )
    }

    suspend fun Element.fetchUserMessageListConverted(@MessageBoxType type: String): List<ComposeMessage> {
        requireNoError()
        val contentPM = select("#contentPM")

        return contentPM.select("table > tbody > tr").filter { it.select("a").isNotEmpty() }.map { item ->
            val avatarUrl = item.select("img.avatar").src()
            val nickname = item.select("img.avatar").attr("title")
            val username = item.select(".sub_title a").hrefId()
            val unread = item.select(".pm_new").isNotEmpty()

            ComposeMessage(
                id = item.select("input[value]").value().parseCount().toLong(),
                title = item.select("a.avatar").text(),
                content = item.select("span.tip").text(),
                time = item.select("small.grey").text(),
                unread = unread,
                type = type,
                user = ComposeUser(
                    id = avatarUrl.avatarUrlId(username),
                    username = username,
                    nickname = nickname,
                    avatar = ComposeImages.fromUrl(avatarUrl)
                )
            )
        }
    }

    suspend fun Element.fetchUserMessageDetailConverted(): ComposeMessageDetail {
        requireNoError()
        val contentPM = select("#contentPM")
        val messages = contentPM.select("#comment_box > .item").map { item ->
            val avatarUrl = item.select(".avatar > span").styleAvatarUrl()

            val textPm = item.select(".text_pm")
            val subjectTitle = textPm.select("hr.board").first()
                ?.previousElementSibling()?.text().orEmpty()

            val (time, id) = textPm.select(".rr").remove().let {
                val time = it.select(".grey").text().substringBefore("/").trim()
                val id = it.select("a[onclick]").attr("onclick").parseCount().toLong()
                time to id
            }

            val (nickname, username) = textPm.select("a.l").remove()
                .let { it.text() to it.hrefId() }

            val html = textPm.html()
                .substringAfter(":")
                .substringAfter("<hr class=\"board\">")

            ComposeMessage(
                id = id,
                title = subjectTitle,
                time = time,
                content = html,
                contentHtml = html.parseAsHtml(),
                user = ComposeUser(
                    id = avatarUrl.avatarUrlId(username),
                    username = username,
                    nickname = nickname,
                    avatar = ComposeImages.fromUrl(avatarUrl)
                )
            )
        }

        val form = contentPM.select("#pmReplyForm")
        val canReply = form.isNotEmpty()

        return ComposeMessageDetail(
            messages = messages.toPersistentList(),
            canReply = canReply,
            msgReceivers = form.select("input[name=msg_receivers]").value(),
            related = form.select("input[name=related]").value(),
            currentMsgId = form.select("input[name=current_msg_id]").value(),
            title = form.select("input[name=msg_title]").value(),
        )
    }

    suspend fun Element.fetchUserHomepageConverted(): ComposeUserStats.Rating {
        requireNoError()
        val userStatsContainers = select("#userStatsContainers")
        val list = listOf(
            userStatsContainers.select("#userStats_all"),
            userStatsContainers.select("#userStats_1"),
            userStatsContainers.select("#userStats_2"),
            userStatsContainers.select("#userStats_3"),
            userStatsContainers.select("#userStats_4"),
            userStatsContainers.select("#userStats_6"),
        )
        val infos = list.mapIndexed { index, item ->
            val gridStats = item.select(".gridStats > .item")
            if (gridStats.size < 6) return@mapIndexed ComposeUserStats.RatingInfo.Empty
            val averageScore = gridStats[3].select(".num").text().toFloat()
            val standardDeviation = gridStats[4].select(".num").text().toFloat()
            val ratingCount = gridStats[5].select(".num").text().toInt()
            val infos = item.select("#ChartWarpper .horizontalChart > li").map { rating ->
                ComposeUserStats.RatingItem(
                    percent = rating.select("a.textTip").attr("title")
                        .substringBefore("%").toFloatOrNull() ?: 0f,
                    label = rating.select(".label").text().parseCount(),
                    count = rating.select(".count").text().parseCount()
                )
            }
            ComposeUserStats.RatingInfo(
                averageScore = averageScore,
                standardDeviation = standardDeviation,
                ratingCount = ratingCount,
                infos = infos.toPersistentList()
            )
        }

        return ComposeUserStats.Rating(
            all = infos[0],
            book = infos[1],
            anime = infos[2],
            music = infos[3],
            game = infos[4],
            real = infos[4],
        )
    }

    suspend fun Element.fetchUserPrivacyConverted(): ComposePrivacy {
        requireNoError()
        val blocklist = select("table.settings")
            .first { it.select("h2").text().contains("绝交") }
            .select("tbody > tr")
            .mapNotNull {
                val a = it.select("a").first()
                if (a == null) null else ComposeUser(
                    username = a.hrefId(),
                    nickname = a.text(),
                    avatar = ComposeImages.fromUrl(userImage(a.hrefId()))
                )
            }
            .toPersistentList()
        return ComposePrivacy(
            blocklist = blocklist
        )
    }

    suspend fun Element.sendUpdateUserInfoConverted() {
        requireNoError()
    }

    private fun List<Pair<String, Element?>>.findValue(title: String): String {
        val element = find { it.first.equals(title, true) }?.second ?: return ""
        val img = element.select("img").first()
        val textarea = element.select("textarea").first()
        val input = element.select("input").first()
        val select = element.select("select [selected]").first()
        return when {
            img != null -> img.attr("src").sanitizeImageUrl()
            textarea != null -> textarea.value()
            input != null -> input.value()
            select != null -> select.value()
            else -> element.text()
        }
    }
}