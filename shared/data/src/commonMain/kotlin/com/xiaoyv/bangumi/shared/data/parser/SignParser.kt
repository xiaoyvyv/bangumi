@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.parserFormHash
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginForm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser

/**
 * [SignParser]
 *
 * @author why
 * @since 2025/1/14
 */
class SignParser : BaseParser() {

    /**
     * 解析登录表单
     */
    fun Element.fetchLoginFormConverted(baseUrl: String): ComposeLoginForm {
        val loginForm = select("#loginForm")
        if (loginForm.isEmpty()) {
            val loginResult = sendLoginConverted()
            if (loginResult.success) {
                return ComposeLoginForm(hasLogin = true, loginInfo = loginResult)
            }
        }

        val elements = hashMapOf<String, String>()
        loginForm.select("input").forEach {
            val name = it.select("input").attr("name")
            val value = it.select("input").attr("value")
            elements[name] = value
        }

        if (elements["referer"].isNullOrBlank()) elements["referer"] = baseUrl
        if (elements["dreferer"].isNullOrBlank()) elements["dreferer"] = baseUrl

        elements.remove("cookietime")
        return ComposeLoginForm(forms = elements.toMap())
    }

    /**
     * 解析登录结果
     */
    fun Element.sendLoginConverted(): ComposeLoginResult {
        val welcome = select("#main #header")
        if (welcome.isNotEmpty()) {
            return ComposeLoginResult(
                success = true,
                message = welcome.select("h1").text().trim(),
                composeUser = parseUserInfo()
            )
        }
        val message = select("#colunmNotice .text").text().trim()
        return ComposeLoginResult(success = false, message = message)
    }

    /**
     * 解析登录成功后的用户信息
     */
    private fun Element.parseUserInfo(): ComposeUser {
        val userId = select(".idBadgerNeue a.avatar").hrefId()
        val avatarUrl = select(".idBadgerNeue a.avatar span").styleAvatarUrl()
        val userName = select("#header a").text()
        val online = select("#header small").text()
        val formHash = parserFormHash()

        return ComposeUser(
            avatar = ComposeImages.fromUrl(avatarUrl),
            nickname = userName,
            username = userId,
            formHash = formHash,
            online = online
        )
    }
}