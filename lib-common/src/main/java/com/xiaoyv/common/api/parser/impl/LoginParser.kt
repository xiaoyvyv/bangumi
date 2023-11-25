package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.LoginFormEntity
import com.xiaoyv.common.api.parser.entity.LoginResultEntity
import org.jsoup.nodes.Document

/**
 * Class: [LoginParser]
 *
 * @author why
 * @since 11/25/23
 */
object LoginParser {

    fun Document.parserLoginForms(): LoginFormEntity {
        val formEntity = LoginFormEntity()
        val loginForm = select("#loginForm")
        if (loginForm.isEmpty()) {
            val loginResult = parseLoginResult()
            if (loginResult.success) {
                formEntity.hasLogin = true
                formEntity.loginInfo = loginResult
                return formEntity
            }
        }

        val elements = hashMapOf<String, String>()
        loginForm.select("input").forEach {
            val name = it.select("input").attr("name")
            val value = it.select("input").attr("value")
            elements[name] = value
        }

        if (elements["referer"].isNullOrBlank()) {
            elements["referer"] = BgmApiManager.URL_BASE_WEB
        }

        if (elements["dreferer"].isNullOrBlank()) {
            elements["dreferer"] = BgmApiManager.URL_BASE_WEB
        }

        elements.remove("cookietime")

        formEntity.forms = elements
        return formEntity
    }

    /**
     * 解析登录结果
     */
    fun Document.parseLoginResult(): LoginResultEntity {
        val columnNotice = select("#colunmNotice")
        val prgGuide = select("#prgGuide")
        if (columnNotice.isEmpty() && prgGuide.isNotEmpty()) {
            val hello = select("#header h1").text().trim()
            return LoginResultEntity(success = true, message = hello)
        }
        val errorMsg = select("#colunmNotice .text").text().trim()
        return LoginResultEntity(success = false, error = errorMsg)
    }
}