@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.IndexItemType
import com.xiaoyv.bangumi.shared.core.utils.formatMills
import com.xiaoyv.bangumi.shared.core.utils.href
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList

@AppParserDsl
class IndexParser(
    private val subjectParser: SubjectParser,
    private val monoParser: MonoParser,
    private val blogParser: BlogParser,
    private val topicParser: TopicTableParser,
) : BaseParser() {

    suspend fun Element.fetchIndexListConverted(): List<ComposeIndex> {
        requireNoError()

        return select(".index-list > ul > li").map { item ->
            val username = item.select("a.avatar").hrefId()
            val avatar = item.select("a.avatar > span").styleAvatarUrl()
            val nickname = item.select(".time > a").first()?.text().orEmpty()

            val createAt = item.select(".tip_j").first()?.text().orEmpty()
            val updateAt = item.select(".tip_j").last()?.text().orEmpty()

            val id = item.select("span.info > .clearit > a.l").hrefLongId()
            val title = item.select(".info h3").text()
            val description = item.select(".desc").text()
            val stats = item.select(".stats")

            // 各个类型的数目
            val webStateMap = mutableMapOf<String, Int>()
            webStateMap[IndexItemType.SUBJECT_TYPE_1] = stats.select("." + IndexItemType.SUBJECT_TYPE_1).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_2] = stats.select("." + IndexItemType.SUBJECT_TYPE_2).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_3] = stats.select("." + IndexItemType.SUBJECT_TYPE_3).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_4] = stats.select("." + IndexItemType.SUBJECT_TYPE_4).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_6] = stats.select("." + IndexItemType.SUBJECT_TYPE_6).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_EP] = stats.select("." + IndexItemType.SUBJECT_TYPE_EP).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_CHARACTER] = stats.select("." + IndexItemType.SUBJECT_TYPE_CHARACTER).text().parseCount()
            webStateMap[IndexItemType.SUBJECT_TYPE_PERSON] = stats.select("." + IndexItemType.SUBJECT_TYPE_PERSON).text().parseCount()

            ComposeIndex(
                id = id,
                title = title,
                desc = description,
                createdAt = createAt.formatMills(),
                updatedAt = updateAt.formatMills(),
                total = webStateMap.values.sum(),
                creator = ComposeUser(
                    id = avatar.avatarUrlId(username),
                    avatar = ComposeImages.fromUrl(avatar),
                    nickname = nickname,
                    username = username
                ),
                category = webStateMap.toImmutableMap()
            )
        }
    }

    suspend fun Element.fetchIndexFocusConverted(): List<ComposeIndexFocus> {
        requireNoError()
        return select("#columnA > .setion > .indexFocus").map { item ->
            ComposeIndexFocus(
                id = item.select(".indexInfo > a").hrefLongId(),
                author = item.select("span.desc").text(),
                title = item.select("h3").text(),
                images = item.select(".coverGrid img")
                    .map { ComposeImages.fromUrl(it.src()) }
                    .toPersistentList()
            )
        }
    }

    suspend fun Element.fetchIndexDetailConverted(id: Long): ComposeIndex {
        requireNoError()
        val main = select("#main")
        val title = main.select("h1").text()
        val columnA = main.select("#columnSubjectBrowserA").first()!!
        val avatarUrl = columnA.select("a.avatar > img").src()
        val username = columnA.select("a.avatar").hrefId()
        val infos = columnA.select("span.tip_j > .tip")

        val nickname = columnA.select(".tip_j > a").text()
        val createAt = infos.getOrNull(0)?.text().orEmpty()
        val updateAt = infos.getOrNull(1)?.text().orEmpty()
        val collectCount = infos.getOrNull(2)?.text().orEmpty().parseCount()
        val description = columnA.select(".line_detail").html()

        val cats = columnA.select("#indexCatBox > ul > li")
            .associate {
                val type = it.select("a").href().substringAfterLast("?", "").trim()
                val count = it.select("small").text().parseCount()
                type to count
            }.toImmutableMap()

        val subjects = with(subjectParser) { columnA.fetchBrowserConverted() }
        val eps = with(subjectParser) { columnA.fetchIndexEpListConverted() }
        val blogs = with(blogParser) { columnA.fetchBrowserBlogConverted() }
        val monos = with(monoParser) { columnA.fetchBrowserMonoConverted() }
        val topics = with(topicParser) { columnA.fetchIndexTopicConverted() }

        return ComposeIndex(
            id = id,
            title = title,
            desc = description,
            createdAt = createAt.formatMills(),
            updatedAt = updateAt.formatMills(),
            creator = ComposeUser(
                id = avatarUrl.avatarUrlId(username),
                avatar = ComposeImages.fromUrl(avatarUrl),
                nickname = nickname,
                username = username
            ),
            total = collectCount,
            category = cats,
            subjects = subjects.toPersistentList(),
            eps = eps.toPersistentList(),
            blogs = blogs.toPersistentList(),
            monos = monos.toPersistentList(),
            topics = topics.toPersistentList(),
        )
    }
}
