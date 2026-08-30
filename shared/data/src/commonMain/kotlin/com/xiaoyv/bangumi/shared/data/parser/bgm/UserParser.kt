@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePrivacy
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmConversation
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmThread
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserServicesEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserStats
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

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
        )
    }

    fun Element.fetchUserEditServicesInfoConverted(): ComposeUserServicesEdit {
        requireLogin()
        val items = select("table.settings > tbody > tr").map {
            it.select("tr td").let { elements ->
                elements.firsTextNode() to elements.getOrNull(1)
            }
        }
        return ComposeUserServicesEdit(
            internetPsn = items.findValue("PSN"),
            internetXbox = items.findValue("Xbox Live"),
            internetSteam = items.findValue("Steam"),
            internetPixi = items.findValue("Pixiv"),
            internetGithub = items.findValue("GitHub"),
            internetTwitter = items.findValue("Twitter"),
            internetIns = items.findValue("Instagram"),
        )
    }


    suspend fun Element.fetchUserPmCoversationConverted(): List<ComposePmConversation> {
        requireNoError()
        val contentPM = select(".pm-conversation-scroll")

        return contentPM.select(".pm-conversation-item ").map { item ->
            val avatarUrl = item.select(".avatarNeue").styleAvatarUrl()
            val nickname = item.select(".pm-conversation-name").text()
            val date = item.select(".pm-conversation-date").text()
            val lastMsg = item.select(".pm-conversation-desc").text()
            val unread = item.select(".pm-conversation-unread").text().parseCount()
            debugLog { "item.hrefLongId()= ${item.hrefLongId()}" }
            ComposePmConversation(
                id = item.hrefLongId(),
                content = lastMsg,
                time = date,
                unread = unread,
                user = ComposeUser(
                    nickname = nickname,
                    avatar = ComposeImages.fromUrl(avatarUrl)
                )
            )
        }
    }

    suspend fun Element.fetchUserPmMessageConverted(): ComposePmMessageDetail {
        requireNoError()
        val contentPM = select(".pm-chat-panel")
        val chatHeader = contentPM.select(".pm-chat-header")
        val chatMessageList = contentPM.select(".pm-message-list")

        val userLink = chatHeader.select(".pm-chat-title strong > a.l")
        val username = userLink.hrefId()
        val avatarUrl = chatHeader.select(".avatarNeue").styleAvatarUrl()

        val threadLinks = chatHeader.select(".pm-thread-filter > a")
        val threads = threadLinks.map { link ->
            ComposePmThread(
                id = link.attr("href")
                    .substringAfter("thread=", "")
                    .substringBefore("&")
                    .parseCount()
                    .toLong(),
                name = link.text(),
            )
        }.toPersistentList()

        val currentThread = threads.getOrNull(threadLinks.indexOfFirst { it.hasClass("focus") })
            ?: ComposePmThread.Empty

        val messages = chatMessageList.select(".pm-thread-label, .pm-message").mapNotNull { item ->
            when {
                item.hasClass("pm-thread-label") -> {
                    ComposePmMessage(title = item.text())
                }

                item.hasClass("pm-message") -> {
                    val messageUserLink = item.select("a.avatar")
                    val messageUsername = messageUserLink.hrefId()
                    val messageAvatarUrl = item.select(".avatarNeue").styleAvatarUrl()
                    val messageTime = item.select(".pm-message-info small")
                        .text()
                        .substringBefore("/")
                        .trim()

                    ComposePmMessage(
                        msgId = item.id().substringAfter("msg_").parseCount().toLong(),
                        content = item.select(".pm-message-body").html(),
                        time = messageTime,
                        user = ComposeUser(
                            id = messageAvatarUrl.avatarUrlId(messageUsername),
                            username = messageUsername,
                            nickname = messageUsername,
                            avatar = ComposeImages.fromUrl(messageAvatarUrl),
                        ),
                    )
                }

                else -> null
            }
        }.toPersistentList()

        val inputs = contentPM.select("#pmReplyForm [name]")
            .associate { input ->
                input.attr("name") to if (input.tagName() == "textarea") input.text() else input.attr("value")
            }
            .toPersistentMap()

        return ComposePmMessageDetail(
            user = ComposeUser(
                id = avatarUrl.avatarUrlId(username),
                username = username,
                nickname = userLink.text(),
                avatar = ComposeImages.fromUrl(avatarUrl),
            ),
            threads = threads,
            messages = messages,
            currentThread = currentThread,
            inputs = inputs,
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
