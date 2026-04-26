@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import androidx.compose.ui.text.AnnotatedString
import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.href
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSectionTitle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeInfobox
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPersonType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPosition
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.JsonPrimitive

@AppParserDsl
class MonoParser(private val commentParser: CommentParser) : BaseParser() {

    suspend fun Element.fetchMonoDetailConverted(): ComposeMonoWebInfo {
        requireNoError()
        val infoList = select("#infobox > li")
        val subInfoList = infoList.select("li.sub_container").remove()
        infoList.removeAll(subInfoList)
        val infoHtml = infoList.toMutableList()
        val subInfoHtml = subInfoList.select("ul > li")
        infoHtml.addAll(if (infoHtml.isNotEmpty()) 1 else 0, subInfoHtml)
        infoHtml.removeAll { it.html().contains("别名") }

        val comments = with(commentParser) { parserBottomComment(CommentType.MONO) }

        val info = infoHtml.joinToString("<br>") { it.infoHtml() }
        val shortInfo = infoHtml.take(10).joinToString("<br>") { it.infoHtml() }

        val columns = select(".columns")
        val indexList = columns.select("#subjectPanelIndex ul > li").map {
            val username = it.attr("data-item-user")
            val avatarUrl = it.select("a.avatar > span").styleAvatarUrl()

            ComposeIndex(
                id = it.select(".innerWithAvatar > a").hrefLongId(),
                title = it.select(".innerWithAvatar > a").text(),
                creator = ComposeUser(
                    id = avatarUrl.avatarUrlId(username),
                    username = username,
                    avatar = ComposeImages.fromUrl(avatarUrl),
                    nickname = it.select("small a").text()
                )
            )
        }

        return ComposeMonoWebInfo(
            info = info,
            indexList = indexList.toPersistentList(),
            shortInfo = shortInfo,
            shortInfoHtml = shortInfo.parseAsHtml(),
            comments = comments.toPersistentList(),
        )
    }

    /**
     * 解析人物首页数据
     */
    suspend fun Element.fetchMonoHomepageConverted(): List<ComposeSection<ComposeMonoDisplay>> {
        requireNoError()
        val sections = mutableListOf<ComposeSection<ComposeMonoDisplay>>()
        val main = select(".mainWrapper")
        val columnSubjectBrowserA = main.select("#columnSubjectBrowserA")
        val columnSubjectBrowserB = main.select("#columnSubjectBrowserB")

        columnSubjectBrowserA.select(".section").forEach { item ->
            val sectionId = item.select(".section > a").href()
            sections.add(
                ComposeSection(
                    key = sectionId,
                    header = ComposeSectionTitle(
                        title = item.select("h2").text(),
                        more = item.select(".section > a").text().replace("»", "").trim(),
                        id = sectionId
                    ),
                    item = ComposeMonoDisplay.Empty
                )
            )
            item.select("ul > li").forEach {
                val link = it.select("a[title]")
                val isCharacter = link.href().contains("character")
                val id = link.hrefLongId()
                sections.add(
                    ComposeSection(
                        key = "$sectionId-" + link.hrefId(),
                        header = ComposeSectionTitle.Empty,
                        item = ComposeMonoDisplay(
                            type = if (isCharacter) MonoType.CHARACTER else MonoType.PERSON,
                            info = ComposeMonoInfo(
                                mono = ComposeMono(
                                    id = id,
                                    name = link.attr("title"),
                                    nameCN = it.select("p > small").text(),
                                    images = ComposeImages.fromUrl(it.select("img").src())
                                )
                            )
                        )
                    )
                )
            }
        }

        columnSubjectBrowserB.select(".sideInner > .subtitle").forEach { title ->
            val side = requireNotNull(title.nextElementSibling())
            val sectionId = title.select("a").href()
            sections.add(
                ComposeSection(
                    key = "sideInner-$sectionId",
                    header = ComposeSectionTitle(
                        title = title.firsTextNode(),
                        more = title.select("small").text(),
                        id = sectionId,
                    ),
                    item = ComposeMonoDisplay.Empty
                )
            )

            side.select("dl").forEach {
                val link = it.select("a[title]")
                val isCharacter = link.attr("href").contains("character")
                val id = link.hrefLongId()
                sections.add(
                    ComposeSection(
                        key = "$sectionId-" + link.hrefId(),
                        header = ComposeSectionTitle.Empty,
                        item = ComposeMonoDisplay(
                            info = ComposeMonoInfo(
                                mono = ComposeMono(
                                    id = id,
                                    type = if (isCharacter) MonoType.CHARACTER else MonoType.PERSON,
                                    name = link.attr("title"),
                                    nameCN = link.attr("title"),
                                    images = ComposeImages.fromUrl(it.select(".avatar > span").styleAvatarUrl())
                                )
                            )
                        )
                    )
                )
            }
        }
        return sections.toPersistentList()
    }

    /**
     * 解析人物浏览的分页数据
     */
    suspend fun Element.fetchBrowserMonoConverted(): List<ComposeMonoDisplay> {
        requireNoError()
        val columnCrtBrowserB = select("#columnCrtBrowserB")
        return columnCrtBrowserB.select(".browserCrtList > div").map { item ->
            val type = if (item.id().contains("character")) MonoType.CHARACTER else MonoType.PERSON
            val avatarUrl = item.select(".avatar > img").src()
            val info = item.select(".prsn_info .tip").text()
            val infoMap = info.split(" / ")
                .map {
                    val strings = it.split(" ")
                    ComposeInfobox(strings.firstOrNull().orEmpty(), JsonPrimitive(strings.lastOrNull().orEmpty()))
                }
                .filterNot { it.key.isNullOrBlank() || it.value == null }

            // 目录内复用解析填充
            item.select("#comment_box").text()
            item.attr("attr-index-related")

            ComposeMonoDisplay(
                type = type,
                info = ComposeMonoInfo(
                    mono = ComposeMono(
                        id = item.select("a.avatar").hrefLongId(),
                        images = ComposeImages.fromUrl(avatarUrl),
                        name = item.select("h3").text(),
                        infobox = infoMap.toPersistentList(),
                        webInfo = ComposeMonoWebInfo(
//                            indexNote = indexNote,
//                            indexRelatedId = indexRelatedId,
//                            commentCount = item.select("small.na").text().parseCount(),
                            info = info,
                            shortInfo = info,
                            shortInfoHtml = AnnotatedString(info)
                        )
                    )
                )
            )
        }
    }

    suspend fun Element.fetchPersonWorkPositionConverted(): List<ComposePersonPosition> {
        requireNoError()
        return select(".subjectFilter > .grouped")
            .first { it.select("li.title").text().trim() == "职位" }
            .select("li > a")
            .map {
                ComposePersonPosition(
                    summary = it.text(),
                    type = ComposePersonPersonType.from(it.hrefLongId(), it.text())
                )
            }
    }
}






