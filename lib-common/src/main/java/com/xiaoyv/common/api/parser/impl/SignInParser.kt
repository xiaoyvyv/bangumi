package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SignInFormEntity
import com.xiaoyv.common.api.parser.entity.SignInResultEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parserFormHash
import com.xiaoyv.common.api.response.UserEntity
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Class: [SignInParser]
 *
 * @author why
 * @since 11/25/23
 */
object SignInParser {

    fun Document.parserLoginForms(): SignInFormEntity {
        val formEntity = SignInFormEntity()
        val loginForm = select("#loginForm")
        if (loginForm.isEmpty()) {
            val loginResult = parserLoginResult()

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
    fun Element.parserLoginResult(): SignInResultEntity {
        val welcome = select("#main #header")
        if (welcome.isNotEmpty()) {
            return SignInResultEntity(
                success = true,
                message = welcome.select("h1").text().trim(),
                userEntity = parseUserInfo()
            )
        }
        val errorMsg = select("#colunmNotice .text").text().trim()
        return SignInResultEntity(
            success = false,
            error = errorMsg,
            userEntity = parseUserInfo()
        )
    }

    /**
     * 解析登录成功后的用户信息
     */
    private fun Element.parseUserInfo(): UserEntity {
        val userId = select(".idBadgerNeue a.avatar").hrefId()
        val avatarUrl = select(".idBadgerNeue a.avatar span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        val userName = select("#header a").text()
        val online = select("#header small").text()
        val formHash = parserFormHash()

        return UserEntity(
            avatar = avatarUrl,
            nickname = userName,
            username = userName,
            id = userId,
            isEmpty = false,
            formHash = formHash,
            online = online
        )
    }

    /**
     * 校验页面是否登录
     */
    fun Document.parserCheckIsLogin(): Boolean {
        val guest = select(".guest").outerHtml()
        val dock = select("#dock").outerHtml()
        val message = select(".message").outerHtml()
        return (dock.contains("login")
                || dock.contains("signup")
                || message.contains("login")
                || message.contains("登录")
                || guest.contains("login")
                || guest.contains("登录")).not()
    }
}

