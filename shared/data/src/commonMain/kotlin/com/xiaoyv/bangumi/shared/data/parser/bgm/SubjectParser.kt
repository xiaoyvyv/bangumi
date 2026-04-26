@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import androidx.compose.ui.text.AnnotatedString
import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.groupValue
import com.xiaoyv.bangumi.shared.core.utils.groupValueOne
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.infoEpsRegex
import com.xiaoyv.bangumi.shared.core.utils.infoMonthDayRegex
import com.xiaoyv.bangumi.shared.core.utils.infoYearMonthDayRegex
import com.xiaoyv.bangumi.shared.core.utils.infoYearMonthRegex
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.parseStar
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.Airtime
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexEp
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlin.math.roundToInt

@AppParserDsl
class SubjectParser : BaseParser() {
    private val cardColors = persistentMapOf(
        "default" to "#F09199",
        "green" to "#70B941",
        "blue" to "#6BAAE8",
        "orange" to "#E68E46",
        "purple" to "#9065ED",
        "sky" to "#369CF8",
    )

    /**
     * 浏览条目页面解析
     */
    suspend fun Element.fetchBrowserConverted(): List<ComposeSubject> {
        requireNoError()
        return select("#browserItemList > li").map { item ->
            val rateInfo = item.select(".rateInfo")

            // 目录内复用解析填充
            val indexNote = item.select("#comment_box").text()
            val indexRelatedId = item.attr("attr-index-related")

            ComposeSubject(
                id = item.select("a.cover").hrefLongId(),
                name = item.select(".inner h3 small").text(),
                nameCn = item.select(".inner h3 a").text(),
                rating = ComposeRating(
                    rank = item.select(".rank").text().parseCount(),
                    score = rateInfo.select(".fade").doubleValue(),
                    total = rateInfo.select(".tip_j").text().parseCount()
                ),
                images = ComposeImages.fromUrl(
                    item.select("a.cover img").attr("src").sanitizeImageUrl()
                ),
                summary = item.select(".info.tip").text().trim(),
                airtime = Airtime(date = "2020-12-12"),
                webInfo = ComposeSubjectWebInfo(
                    indexNote = indexNote,
                    indexRelatedId = indexRelatedId
                ),
                type = item.select(".ico_subject_type").toString().let { clsName: String ->
                    when {
                        clsName.contains("subject_type_1") -> SubjectType.BOOK
                        clsName.contains("subject_type_2") -> SubjectType.ANIME
                        clsName.contains("subject_type_3") -> SubjectType.MUSIC
                        clsName.contains("subject_type_4") -> SubjectType.GAME
                        clsName.contains("subject_type_6") -> SubjectType.REAL
                        else -> SubjectType.UNKNOWN
                    }
                }
            )
        }
    }

    suspend fun Element.fetchSubjectDetailConverted(): ComposeSubjectWebInfo {
        requireNoError()
        val columns = select(".columns")
        val infoList = columns.select("#infobox > li")
        val subInfoList = infoList.select("li.sub_container").remove()
        infoList.removeAll(subInfoList)
        val infoHtml = infoList.toMutableList()
        val subInfoHtml = subInfoList.select("ul > li")
        infoHtml.addAll(subInfoHtml)

        val info = infoHtml.joinToString("<br>") {
            it.html().replace(":\\s+".toRegex(), "：")
        }
        val shortInfo = infoHtml.take(10).joinToString("<br>") {
            it.html().replace(":\\s+".toRegex(), "：")
        }

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
        return ComposeSubjectWebInfo(
            info = info,
            shortInfo = shortInfo,
            shortInfoHtml = shortInfo.parseAsHtml(),
            indexList = indexList.toPersistentList()
        )
    }

    suspend fun Element.fetchSubjectCommentConverted(): List<ComposeComment> {
        requireNoError()
        return select("#comment_box > .item").map {
            val avatarUrl = it.select("a.avatar > span").styleAvatarUrl()
            val comment = it.select(".comment").text()
            val text = it.select(".text small").text()

            ComposeComment(
                id = it.attr("data-item-user"),
                type = CommentType.SUBJECT,
                comment = comment,
                commentHtml = comment.parseAsHtml(),
                time = text.substringAfterLast("@").trim(),
                collectType = CollectionType.from(text.substringBefore("@")),
                star = it.parseStar(),
                user = ComposeUser(
                    id = avatarUrl.avatarUrlId(it.attr("data-item-user")),
                    username = it.attr("data-item-user"),
                    avatar = ComposeImages.fromUrl(avatarUrl),
                    nickname = it.select(".text a.l").text()
                ),
            )
        }
    }

    suspend fun Element.fetchIndexEpListConverted(): List<ComposeIndexEp> {
        requireNoError()

        return select("ul.browserList > li").map { item ->
            val indexNote = item.select("#comment_box").text()
            val indexRelatedId = item.attr("attr-index-related")

            ComposeIndexEp(
                id = item.select("a.avatar").hrefLongId(),
                name = item.select("h3").text(),
                indexNote = indexNote,
                indexRelatedId = indexRelatedId,
                subject = ComposeSubject(
                    name = item.select(".inner > a > .tip").text(),
                    nameCn = item.select(".inner > a > .tip_j").text(),
                    id = item.select(".inner > a").hrefLongId(),
                    images = ComposeImages.fromUrl(item.select("img.avatar").src())
                )
            )
        }
    }

    /**
     * 用户收藏的条目解析，没有用API因为，API暂未提供WEB的排序过滤功能
     */
    suspend fun Element.fetchUserCollectionCoverted(
        @SubjectType subjectType: Int,
        @CollectionType collectionType: Int,
    ): List<ComposeSubjectDisplay> {
        requireNoError()

        return select("#browserItemList > li").map { item ->
            val id = item.select("a.cover").hrefLongId()
            val coverImage = item.select("a.cover img").attr("src").sanitizeImageUrl()
            val name = item.select(".inner h3 small").text()
            val nameCn = item.select(".inner h3 a").text()
            val infoTip = item.select(".info.tip").text()

            val yearMonth = infoYearMonthRegex.groupValue(infoTip, 0)
            val monthDay = infoMonthDayRegex.groupValue(infoTip, 0)
            val eps = infoEpsRegex.groupValueOne(infoTip).parseCount()
            val info = infoTip
                .replace(infoYearMonthDayRegex, "")
                .replace(infoEpsRegex, "")
                .split("/")
                .filter { it.isNotBlank() }
                .joinToString(" / ") { it.trim() }

            val collectInfo = item.select(".collectInfo")
            val collectionTime = collectInfo.select(".tip_j").text()
            val collectionRate = collectInfo.parseStar().roundToInt()
            val collectionComment = item.select("#comment_box .text").text()
            val collectionTags = collectInfo.select(".tip").text()
                .replace("标签: ", "")
                .split(" ")
                .filter { it.isNotBlank() }

            ComposeSubjectDisplay(
                subject = ComposeSubject(
                    id = id,
                    images = ComposeImages.fromUrl(coverImage),
                    name = name,
                    nameCn = nameCn,
                    type = subjectType,
                    info = info,
                    airtime = Airtime(date = yearMonth.ifBlank { monthDay }),
                    eps = eps,
                    webInfo = ComposeSubjectWebInfo(
                        info = info,
                        shortInfo = info,
                        shortInfoHtml = AnnotatedString(info)
                    )
                ),
                collection = if (collectInfo.isEmpty()) ComposeCollection.Empty else ComposeCollection(
                    comment = collectionComment,
                    type = collectionType,
                    tags = collectionTags.toPersistentList(),
                    subjectType = subjectType,
                    rate = collectionRate,
                    updatedAt = collectionTime
                )
            )
        }
    }

    suspend fun Element.fetchCharacterAlbumCoverted(): List<ComposeGallery> {
        requireNoError()
        return select("#columnCrtB > ul.photoList >li").map {
            ComposeGallery(
                id = it.select("a").hrefId(),
                image = it.select("img").src(),
                original = it.select("img").src(),
                info = it.select(".info").text(),
            )
        }
    }

    suspend fun Element.fetchSubjectStatsConverted(): ComposeSubjectStats {
        requireNoError()

        val mainWrapper = select(".mainWrapper")
        val columnInSubjectA = mainWrapper.select("#columnInSubjectA")
        val json = "CHART_SETS\\s*=\\s*([\\s\\S]+?);".toRegex().groupValueOne(mainWrapper.html())

        val stats = json.fromJson<ComposeSubjectStats>() ?: return ComposeSubjectStats.Empty

        val linkMap = linkedMapOf<String, String>()
        // 增加 想看 类型对应为 1
        linkMap["想看"] = "1"
        linkMap.putAll(stats.interestType.seriesSet)

        val gridStats = columnInSubjectA.select(".gridStats")
        val mapItem = { item: Element ->
            val colorText = item.className().replace("item", "").trim().ifBlank { "default" }
            ComposeSubjectStats.GridState(
                id = System.currentTimeMillis().toString(),
                title = item.select(".num").text(),
                desc = item.select(".desc").text(),
                color = cardColors.getOrElse(colorText) { "#999999" }
            )
        }

        return stats.copy(
            interestGridState = gridStats.firstOrNull()?.select(".item")?.map(mapItem).orEmpty().toPersistentList(),
            vibGridState = gridStats.lastOrNull()?.select(".item")?.map(mapItem).orEmpty().toPersistentList(),
            interestType = stats.interestType.copy(seriesSet = linkMap.toPersistentMap()),
            totalCollects = stats.totalCollects.copy(desc = "按用户「看过」总量"),
            vib = stats.vib.copy(desc = "Very Important Bangumier / Beta 2 Release"),
            relativeRegdate = stats.regDate.copy(desc = "注册 X 天内评分")
        )
    }

    suspend fun Element.fetchSearchSubjectTagsConverted(): List<ComposeTag> {
        requireNoError()
        return select("#tagList > a").map {
            ComposeTag(name = it.text(), count = it.nextElementSibling()?.text().parseCount())
        }
    }

    suspend fun Element.fetchMySubjectTagsCovConverted(): List<ComposeTag> {
        requireNoError()
        return select(".tagList")
            .last { it.select("span").text().contains("我的标签") }
            .select("div > a").map {
                ComposeTag(name = it.text(), count = 0)
            }
    }
}