package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MonoEntity
import com.xiaoyv.common.api.parser.firsTextNode
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.styleBackground
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SampleImageEntity
import org.jsoup.nodes.Element

/**
 * Mono
 *
 * @author why
 * @since 12/19/23
 */
fun Element.parserMono(): MonoEntity {
    val monoEntity = MonoEntity()

    select("#columnChlCrtA > .section, #columnA > .section").forEach { section ->
        val grid = MonoEntity.Grid(
            title = section.select(".title").text().trimStart('我'),
            items = section.select("ul > li").map { item ->
                val entity = SampleImageEntity()
                val isCharacter = item.select("a").attr("href").contains(BgmPathType.TYPE_CHARACTER)
                entity.id = item.select("a").hrefId()
                entity.type =
                    if (isCharacter) BgmPathType.TYPE_CHARACTER else BgmPathType.TYPE_PERSON
                entity.title = item.select("a[title]").attr("title")
                entity.desc = item.select("small").text()
                entity.image = item.select("img").attr("src").optImageUrl()
                entity
            }
        )
        monoEntity.grids.add(grid)
    }

    select("#columnChlCrtB .side").forEach { side ->
        val grid = MonoEntity.Grid(
            title = side.select("h2").firsTextNode(),
            items = side.select("div > dl").map { item ->
                val entity = SampleImageEntity()
                val isCharacter = item.select("a").attr("href").contains(BgmPathType.TYPE_CHARACTER)
                entity.id = item.select("a").hrefId()
                entity.type =
                    if (isCharacter) BgmPathType.TYPE_CHARACTER else BgmPathType.TYPE_PERSON
                entity.title = item.select("a[title]").attr("title")
                entity.desc = item.select("dd a").text()
                entity.image = item.select("a.avatar > span").styleBackground()
                entity
            }
        )
        monoEntity.grids.add(grid)
    }
    return monoEntity
}