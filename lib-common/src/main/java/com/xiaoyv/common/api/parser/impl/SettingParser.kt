package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.exception.NeedLoginException
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.api.parser.impl.LoginParser.parserCheckIsLogin
import com.xiaoyv.common.config.annotation.EditProfileOptionType
import com.xiaoyv.widget.kts.copyAddAll
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/27/23
 */
fun Document.parserSettingInfo(): List<SettingBaseEntity> {
    val loginState = parserCheckIsLogin()
    if (loginState.not()) throw NeedLoginException("请先登录后才能继续进行")

    val editInputs = select("form table.settings > tbody > tr").map {
        val superEntity = SettingBaseEntity()
        superEntity.title = it.select("td").firstOrNull()?.text().orEmpty()
        val input = it.select("input")
        if (input.isNotEmpty()) {
            superEntity.value = input.attr("value")
            superEntity.field = input.select("name").text()
            superEntity.type = input.attr("type").orEmpty()
        } else {
            val select = it.select("select")
            superEntity.field = select.select("name").text()
            superEntity.type = EditProfileOptionType.TYPE_SELECTOR
            superEntity.options = it.select("option").map { option ->
                val selectItem = SettingBaseEntity.SelectItem()
                selectItem.title = option.text()
                selectItem.value = option.attr("value")
                selectItem.isSelected = option.attr("selected").let { selected ->
                    selected == "selected"
                }
                selectItem
            }
            superEntity.value =
                superEntity.options.find { item -> item.isSelected }?.value.orEmpty()
        }
        superEntity
    }

    val hiddenList = select("form > input").map {
        val superEntity = SettingBaseEntity()
        superEntity.value = it.attr("value")
        superEntity.field = it.select("name").text()
        superEntity.type = it.attr("type").orEmpty()
        superEntity
    }

    return editInputs.copyAddAll(hiddenList)
}