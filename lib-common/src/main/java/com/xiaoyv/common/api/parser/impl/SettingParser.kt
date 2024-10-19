package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.exception.NeedLoginException
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.api.parser.impl.SignInParser.parserCheckIsLogin
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.config.annotation.FormInputType
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/27/23
 */
fun Document.parserSettingInfo(): List<SettingBaseEntity> {
    val loginState = parserCheckIsLogin()
    if (loginState.not()) throw NeedLoginException(i18n(CommonString.common_login_first))

    return select("#main form input, #main form textarea, #main form select").map {
        val superEntity = SettingBaseEntity()
        when (it.tagName().lowercase()) {
            "input" -> {
                superEntity.title = it.parent()?.parent()?.firstElementChild()?.text().orEmpty()
                superEntity.value = it.attr("value")
                superEntity.field = it.attr("name")
                superEntity.type = it.attr("type").lowercase()

                if (superEntity.type == FormInputType.TYPE_FILE) {
                    val imageUrls = it.parent()?.select("img").orEmpty()
                        .map { element ->
                            element.attr("src").orEmpty().optImageUrl()
                        }
                    superEntity.value = imageUrls.firstOrNull().orEmpty()
                }
            }

            "select" -> {
                superEntity.title = it.parent()?.parent()?.firstElementChild()?.text().orEmpty()
                superEntity.type = FormInputType.TYPE_SELECT
                superEntity.field = it.attr("name")
                superEntity.options = it.select("option").map { option ->
                    val selectItem = SettingBaseEntity.SelectItem()
                    selectItem.title = option.text()
                    selectItem.value = option.attr("value")
                    selectItem.isSelected = option.attr("selected").let { selected ->
                        selected == "selected"
                    }
                    selectItem
                }
                superEntity.value = superEntity.options.find { item ->
                    item.isSelected
                }?.value.orEmpty()
            }

            "textarea" -> {
                superEntity.title = it.parent()?.parent()?.firstElementChild()?.text().orEmpty()
                superEntity.value = it.text()
                superEntity.field = it.attr("name")
                superEntity.type = FormInputType.TYPE_INPUT
            }

            else -> return@map null
        }
        superEntity
    }.filterNotNull()
}

fun Document.parserSettingUpdateResult(): String {
    return select(".message p.text").text()
}