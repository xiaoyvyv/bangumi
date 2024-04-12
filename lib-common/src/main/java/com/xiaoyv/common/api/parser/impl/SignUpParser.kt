package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SignUpResultEntity
import com.xiaoyv.common.api.parser.entity.SignUpVerifyEntity
import org.jsoup.nodes.Document

fun Document.parserSignUpResult(): SignUpResultEntity {
    val form = select("#verifyEmailTokenForm")
    val welcome = select("#columnLoginA > p")

    if (welcome.isNotEmpty() && form.isNotEmpty()) {
        val formMap = hashMapOf<String, String>()

        form.select("input").forEach { item ->
            val name = item.attr("name")
            val value = item.attr("value")
            formMap[name] = value
        }

        return SignUpResultEntity(
            success = true,
            message = welcome.joinToString("\n\n") { it.text().trim() },
            forms = formMap
        )
    }
    val errorMsg = select("#colunmNotice .text").text().trim()
    return SignUpResultEntity(
        success = false,
        error = errorMsg,
    )
}

fun Document.parserSignUpVerify(): SignUpVerifyEntity {
    val welcome = select("#main #header")
    if (welcome.isNotEmpty()) {
        return SignUpVerifyEntity(
            success = true,
            message = welcome.select("h1").text().trim(),
        )
    }
    val errorMsg = select("#colunmNotice .text").text().trim()
    return SignUpVerifyEntity(
        success = false,
        error = errorMsg,
    )
}

