package com.xiaoyv.bangumi.shared.data.parser

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.parse_data_none
import com.xiaoyv.bangumi.shared.core.exception.ApiException
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.utils.fetchStyleBackgroundUrl
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmojiParam
import org.jetbrains.compose.resources.getString

/**
 * [BaseParser]
 *
 * @author why
 * @since 2025/1/14
 */
@AppParserDsl
open class BaseParser {

    /**
     * 校验页面是否登录
     */
    fun Element.requireLogin() {
        if (checkIsLogin()) return
        throw ApiHttpException(code = 401, bodyAsText = select(".message").text())
    }

    suspend fun <T : Element> T.requireNoError() {
        RobotSpeech.instance.add(select("#robot_speech").text().trim())

        val errorMsg = select("#colunmNotice .text").text().trim()
        if (errorMsg.isNotBlank()) {
            throw ApiException(errorMsg.ifBlank { getString(Res.string.parse_data_none) })
        }
    }

    fun String?.parseCount(): Int {
        val orNull = this?.toIntOrNull()
        if (orNull != null) return orNull
        return "(\\d+)".toRegex().find(this ?: return 0)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
    }

    fun Elements.text(index: Int): String {
        return getOrNull(index)?.text().orEmpty()
    }

    fun Elements.doubleValue() = text().toDoubleOrNull() ?: 0.0
    fun Element.doubleValue() = text().toDoubleOrNull() ?: 0.0

    fun Elements.src() = attr("src").sanitizeImageUrl()
    fun Element.src() = attr("src").sanitizeImageUrl()

    fun Elements.styleAvatarUrl() = attr("style")
        .fetchStyleBackgroundUrl()
        .sanitizeImageUrl()

    fun Element.styleAvatarUrl() = attr("style")
        .fetchStyleBackgroundUrl()
        .sanitizeImageUrl()

    fun String.avatarUrlId(username: String): Long = substringAfterLast("/")
        .substringBefore("?")
        .parseCount()
        .takeIf { it > 0 }?.toLong() ?: username.toLongOrNull() ?: 0L

    fun Element.checkIsLogin(): Boolean {
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

    /**
     * 解析添加贴贴的参数信息
     *
     * ```
     *   <a href="javascript:void(0);" class="icon like_dropdown" data-like-type="8" data-like-main-id="391190"
     *      data-like-related-id="2557602" data-like-tpl-id="likes_reaction_menu">
     *     <span class="ico ico_like">&nbsp;</span>
     *     <span class="title">贴贴</span>
     *   </a>
     * ```
     */
    fun Elements.parserLikeParam(): ComposeEmojiParam {
        val element = select("a[data-like-type]")
        return ComposeEmojiParam(
            enable = element.isNotEmpty(),
            likeType = element.attr("data-like-type"),
            likeMainId = element.attr("data-like-main-id"),
            likeCommentId = element.attr("data-like-related-id")
        )
    }

    fun Element.infoHtml(): String {
        return html()
            .replace(":\\s+".toRegex(), "：")
            .replace("英文名", "英文名：")
            .replace("纯假名", "纯假名：")
            .replace("日文名", "日文名：")
            .replace("罗马字", "罗马字：")
            .replace("昵称", "昵称：")
            .replace("其他名义", "其他名义：")
    }
}