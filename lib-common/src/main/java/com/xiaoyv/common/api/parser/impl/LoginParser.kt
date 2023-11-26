package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.LoginFormEntity
import com.xiaoyv.common.api.parser.entity.LoginResultEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.response.UserEntity
import com.xiaoyv.widget.kts.orEmpty
import org.jsoup.nodes.Document

/**
 * Class: [LoginParser]
 *
 * @author why
 * @since 11/25/23
 */
object LoginParser {

    fun Document.parserLoginForms(email: String, password: String): LoginFormEntity {
        val formEntity = LoginFormEntity()
        val loginForm = select("#loginForm")
        if (loginForm.isEmpty()) {
            val loginResult = parserLoginResult(email, password)
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
    fun Document.parserLoginResult(email: String, password: String): LoginResultEntity {
        val columnNotice = select("#colunmNotice")
        val prgGuide = select("#prgGuide")
        if (columnNotice.isEmpty() && prgGuide.isNotEmpty()) {
            val hello = select("#header h1").text().trim()
            return LoginResultEntity(
                success = true,
                message = hello,
                userEntity = parseUserInfo(email, password)
            )
        }
        val errorMsg = select("#colunmNotice .text").text().trim()
        return LoginResultEntity(
            success = false,
            error = errorMsg,
            userEntity = parseUserInfo(email, password)
        )
    }

    /**
     * 解析用户信息
     */
    private fun Document.parseUserInfo(email: String, password: String): UserEntity {
        val userId = select(".idBadgerNeue a.avatar").attr("href").substringAfterLast("/")
            .toLongOrNull().orEmpty()
        val avatarUrl = select(".idBadgerNeue a.avatar span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        val userName = select("#header a").text()
        val online = select("#header small").text()

        return UserEntity(
            avatar = UserEntity.Avatar(avatarUrl, avatarUrl, avatarUrl),
            nickname = userName,
            username = userName,
            id = userId,
            isLogout = false,
            online = online,
            email = email,
            password = password
        )
    }

    /**
     * 校验页面是否登录
     */
    fun Document.parserLoginState(): Boolean {
        val html = select("#dock").outerHtml()
        return !(html.contains("login") || html.contains("signup"))
    }
}

