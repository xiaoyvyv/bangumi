package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.notification_no_data
import com.xiaoyv.bangumi.shared.core.exception.ApiException
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.href
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import org.jetbrains.compose.resources.getString


@AppParserDsl
class NotificationParser : BaseParser() {

    suspend fun Element.fetchUserNotificationConverted(checkEmpty: Boolean): List<ComposeNotification> {
        requireNoError()

        val elements = select("#comment_list > div")
        if (checkEmpty && elements.isEmpty()) {
            throw ApiException(getString(Res.string.notification_no_data))
        }

        return elements.map { item ->
            val username = item.attr("data-item-user")
            val avatarUrl = item.select("a.avatar > span").styleAvatarUrl()
            val userId = avatarUrl.avatarUrlId(username)
            val nickname = item.select("strong > a").text()
            val reply = item.select(".reply_content")
            val link = reply.select("a").href()
            val replyContent = reply.html()
            val count = item.select(".merge_count").text().parseCount()

            ComposeNotification(
                id = item.id().parseCount().toLong(),
                link = link,
                count = count,
                user = ComposeUser(
                    id = userId,
                    nickname = nickname,
                    username = username,
                    avatar = ComposeImages.fromUrl(avatarUrl)
                ),
                message = replyContent,
                messageHtml = replyContent.parseAsHtml()
            )
        }.distinctBy { it.id }
    }

    suspend fun Element.fetchUserUnreadMessageConverted(): ComposeUnRead {
        requireNoError()
        return ComposeUnRead(count = select("#pm_sidebar").firsTextNode().parseCount())
    }
}