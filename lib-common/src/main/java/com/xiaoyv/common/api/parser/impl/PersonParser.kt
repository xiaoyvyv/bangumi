package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.widget.star.StarCommentView
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/4/23
 */
fun Document.parserPerson(personId: String, isVirtual: Boolean): PersonEntity {
    val entity = PersonEntity()
    entity.id = personId
    entity.isVirtual = isVirtual

    select("#headerSubject").apply {
        entity.nameCn = select("small").text()
        entity.nameNative = select(".nameSingle a").attr("title")
        // /person/17491/collect?gh=275b091c
        // /person/17491/erase_collect?gh=275b091c
        select(".collect a").apply {
            if (isEmpty()) {
                entity.isCollected = false

            } else {
                attr("href").apply {
                    entity.isCollected = !contains("erase_collect")
                    entity.gh = substringAfterLast("=")
                }
            }
        }
    }

    select("#columnCrtA").apply {
        entity.poster = select("img.cover").attr("src").optImageUrl()
        entity.posterLarge = select("a.cover").attr("href").optImageUrl()
        entity.infos = select(".infobox_container li").map { item ->
            val subInfo = PersonEntity.SubInfo()
            subInfo.title = item.select(".tip").remove().text().trim().removeSuffix(":")
            subInfo.value = item.text()
            subInfo
        }

        entity.recommendIndexes = select("#subjectPanelIndex .groupsLine > li").map { item ->
            val mediaIndex = MediaDetailEntity.MediaIndex()

            item.select(".innerWithAvatar a.avatar").apply {
                mediaIndex.id = attr("href").substringAfterLast("/")
                mediaIndex.title = text()
            }
            item.select(".innerWithAvatar small.grey a").apply {
                mediaIndex.userId = attr("href").substringAfterLast("/")
                mediaIndex.userName = text()
            }
            mediaIndex.userAvatar = item.select("li > a.avatar > span")
                .attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
            mediaIndex
        }

        entity.whoCollects = select("#crtPanelCollect .groupsLine > li").map { item ->
            val who = MediaDetailEntity.MediaWho()
            item.select(".innerWithAvatar a.avatar").apply {
                who.userId = attr("href").substringAfterLast("/")
                who.userName = text()
            }
            who.userAvatar = item.select("li > a.avatar span")
                .attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()

            who.star = item.select(".starstop-s > span")
                .attr("class").let { starClass ->
                    StarCommentView.parseScore(starClass)
                }
            who.time = item.select(".innerWithAvatar small").text()
            who
        }
    }

    select("#columnCrtB").apply {
        entity.job = select("h2").firstOrNull()?.text().orEmpty()
        entity.summary = select(".detail").html().parseHtml()
    }

    select(".browserList").associateBy {
        it.previousElementSibling()?.text().orEmpty()
    }.forEach { (key, value) ->
        when {
            isVirtual -> {
                entity.performers = value.select("ul.browserList > li").orEmpty().map { item ->
                    val character = MediaDetailEntity.MediaCharacter()
                    val media = MediaDetailEntity.MediaRelative()

                    item.select(".innerLeftItem").apply {
                        media.id = select("a.subjectCover").attr("href").substringAfterLast("/")
                        media.cover = select("img.cover").attr("src").optImageUrl()
                        media.titleNative = select("h3").text()
                        media.titleCn = select("small.grey").text()
                        media.type = select(".ico_subject_type").toString().let {
                            when {
                                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                                else -> MediaType.TYPE_UNKNOWN
                            }
                        }
                        character.jobs = select(".badge_job").map { it.text() }
                    }

                    item.select(".innerRightList").apply {
                        character.id = select("a.avatar").attr("href").substringAfterLast("/")
                        character.avatar = select("img.avatar").attr("src").optImageUrl()
                        character.characterName = select("h3 a.l").text()
                        character.characterNameCn = select("h3 a.l").text()
                        character.personJob = select("small.grey").text()
                    }
                    PersonEntity.RecentlyPerformer(character, media)
                }
            }

            key.contains("最近演出角色") -> {
                entity.recentCharacters = value.parserPersonVoices()
            }

            key.contains("最近参与") -> {
                entity.recentOpuses = value.select("ul.browserList > li").orEmpty().map { item ->
                    val opus = PersonEntity.RecentlyOpus()
                    item.select(".innerLeftItem").apply {
                        opus.id = select("a.cover").attr("href").substringAfterLast("/")
                        opus.cover = select("img.cover").attr("src").optImageUrl()
                        opus.titleNative = select("h3 a.l").text()
                        opus.jobs = select(".badge_job").map { it.text() }
                        opus.mediaType = select(".ico_subject_type").toString().let {
                            when {
                                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                                else -> MediaType.TYPE_UNKNOWN
                            }
                        }
                    }
                    opus
                }
            }
        }
    }

    entity.recentCooperates = select(".content_inner ul > li").map { item ->
        val cooperate = PersonEntity.RecentCooperate()
        cooperate.id = item.select("a.avatar").attr("href").substringAfterLast("/")
        cooperate.avatar = item.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        cooperate.name = item.select("p.info").text()
        cooperate.times = item.select("small.fade").text().parseCount()
        cooperate
    }
    entity.comments = parserBottomComment()
    return entity
}

/**
 * 解析收藏者者数据
 */
fun Element.parserPersonCollector(): List<MediaDetailEntity.MediaWho> {
    return select("#memberUserList > li.user").map { item ->
        val who = MediaDetailEntity.MediaWho()
        item.select(".userImage img").apply {
            who.userAvatar = attr("src").optImageUrl()
            who.userName = attr("alt")
        }
        who.userId = item.select(".userContainer a").attr("href").substringAfterLast("/")
        who.time = item.select(".info").text()
        who
    }
}

/**
 * 解析合作者数据
 */
fun Element.parserPersonCooperate(): List<PersonEntity.RecentCooperate> {
    return select(".browserCrtList > div").map { item ->
        val cooperate = PersonEntity.RecentCooperate()

        item.select("a.avatar").apply {
            cooperate.avatar = select("img").attr("src").optImageUrl()
            cooperate.id = attr("href").substringAfterLast("/")
        }

        item.select("h3").apply {
            cooperate.name = select("a").text()
            cooperate.times = select("small").text().parseCount()
        }

        item.select(".prsn_info").apply {
            cooperate.jobs = select(".badge_job").map { it.text() }
            cooperate.infos = select(".tip").text()
                .split("/")
                .map {
                    it.trim().split("\\s".toRegex()).let { list ->
                        list.getOrNull(0).orEmpty().trim() to list.getOrNull(1).orEmpty().trim()
                    }
                }
            cooperate.opus = select(".subject_tag_section > a").map { subItem ->
                val relative = MediaDetailEntity.MediaRelative()
                relative.id = subItem.attr("href").substringAfterLast("/")
                relative.titleCn = subItem.attr("title")
                relative.titleNative = subItem.text()
                relative
            }
        }
        cooperate
    }
}

/**
 * 解析作品条目数据
 */
fun Element.parserPersonOpus(): List<PersonEntity.RecentlyOpus> {
    return select("#browserItemList > li").map { item ->
        val opus = PersonEntity.RecentlyOpus()
        opus.id = item.select("a.subjectCover").attr("href").substringAfterLast("/")
        opus.cover = item.select("img.cover").attr("src").optImageUrl()
        opus.titleCn = item.select("h3 a").text()
        opus.titleNative = item.select("small.grey").text()
        opus.mediaType = item.select(".ico_subject_type").toString().let {
            when {
                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                else -> MediaType.TYPE_UNKNOWN
            }
        }
        opus.jobs = item.select(".badge_job").map { it.text() }
        opus.rateInfo = item.select(".rateInfo").let { subItem ->
            val rateInfo = PersonEntity.RateInfo()
            rateInfo.rate = subItem.select(".fade").text().toFloatOrNull() ?: 0f
            rateInfo.count = subItem.select(".tip_j").text().parseCount()
            rateInfo
        }

        opus
    }
}

/**
 * 解析角色的条目数据
 */
fun Element.parserPersonVoices(): List<CharacterEntity> {
    return select(".browserList > li").map { item ->
        val entity = CharacterEntity()

        item.select(".innerLeftItem").apply {
            entity.id = select("a.avatar").attr("href").substringAfterLast("/")
            entity.avatar = select("img.avatar").attr("src").optImageUrl()
            entity.nameNative = select("h3 a.l").text()
            entity.nameCn = select("h3 .tip").text()
        }

        entity.from = item.select(".innerRightList > li").map { subItem ->
            val relative = MediaDetailEntity.MediaRelative()
            relative.id = subItem.select("a.subjectCover").attr("href").substringAfterLast("/")
            relative.cover = subItem.select("img.cover").attr("src").optImageUrl()
            relative.titleNative = subItem.select("h3").text()
            relative.titleCn = subItem.select("small.grey").text()
            relative.characterJobs = subItem.select(".badge_job").map { it.text() }
            relative
        }
        entity
    }
}















