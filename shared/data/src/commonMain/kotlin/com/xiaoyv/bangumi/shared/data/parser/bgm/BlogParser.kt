package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.utils.formatMills
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.data.constant.subjectImage
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toPersistentList

@AppParserDsl
class BlogParser : BaseParser() {

    suspend fun Element.fetchBrowserBlogConverted(): List<ComposeBlogDisplay> {
        requireNoError()
        return select("#entry_list > .item").map { item ->
            val img = item.select(".cover a img")
            val image = img.src()
            val isSubjectCover = img.hasClass("avatarCoverPortrait")
            val title = item.select("h2.title").text()
            val id = item.select("h2.title a").hrefLongId()

            val tls = item.select(".time > a.l")
            val nickname = tls.first()?.text().orEmpty()
            val username = tls.first()?.hrefId().orEmpty()
            val commentCnt = tls.last()?.text().orEmpty().parseCount()

            // 3个情况是正常日志列表，其它是目录内解析填充，没有条目数据
            val subject: ComposeSubject
            if (tls.size == 3) {
                val subjectId = tls[1].hrefLongId()
                val subjectName = tls[1].text()
                subject = ComposeSubject(
                    id = subjectId,
                    images = ComposeImages.fromUrl(if (isSubjectCover) image else subjectImage(subjectId, "medium")),
                    name = subjectName
                )
            } else {
                subject = ComposeSubject.Empty
            }

            val time = item.select(".time").let {
                it.select("a").remove()
                it.text()
                    .substringBeforeLast("·")
                    .substringAfterLast("·")
                    .trim()
            }
            val content = item.select(".content").text()
            val tags = item.select(".tags").text().split(" ").map { it.trim() }

            // 目录内复用解析填充
            val indexNote = item.select("#comment_box").text()
            val indexRelatedId = item.attr("attr-index-related")

            ComposeBlogDisplay(
                id = id,
                tags = tags.toPersistentList(),
                user = ComposeUser(
                    username = username,
                    nickname = nickname,
                    avatar = ComposeImages.fromUrl(if (isSubjectCover) userImage(username) else image)
                ),
                blog = ComposeBlogEntry(
                    id = id,
                    title = title,
                    updatedAt = time.formatMills(),
                    summary = content,
                    replies = commentCnt,
                    icon = subject.images.displayMediumImage
                ),
                indexNote = indexNote,
                indexRelatedId = indexRelatedId,
            )
        }
    }
}