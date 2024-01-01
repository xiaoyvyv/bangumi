package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.lastTextNode
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parserLikeParam
import com.xiaoyv.common.api.parser.preHandleHtml
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * 解析文章的评论
 *
 * @author why
 * @since 12/1/23
 */
fun Element.parserBottomComment(): List<CommentTreeEntity> {
    // 解析 gh
    val filterDeleteComment = ConfigHelper.isFilterDeleteComment
    val isFilterBreakUpComment = ConfigHelper.isFilterBreakUpComment
    val blockUsers = UserHelper.blockUsers

    debugLog { "绝交用户过滤：${isFilterBreakUpComment}：" + blockUsers.toJson(true) }

    // 解析评论
    return select("#comment_list > div").mapCommentItems(
        filterDeleteComment,
        isFilterBreakUpComment,
        blockUsers
    )
}

/**
 * 解析发回复表单
 */
fun Element.parserReplyForm(): CommentFormEntity {
    val formEntity = CommentFormEntity()
    select("#ReplyForm input").forEach {
        formEntity.inputs[it.attr("name")] = it.attr("value")
    }
    formEntity.action = select("#ReplyForm").attr("action")
    return formEntity
}

/**
 * 解析评论
 *
 * @param gh 表单
 * @param filterDeleteComment 是否过滤删除的评论
 * @param filterBlockUserComment 是否过滤屏蔽用户评论
 * @param blockUsers 屏蔽的用户
 */
private fun Elements.mapCommentItems(
    filterDeleteComment: Boolean,
    filterBlockUserComment: Boolean,
    blockUsers: List<String>,
): List<CommentTreeEntity> {
    val entities = arrayListOf<CommentTreeEntity>()

    forEach { item ->
        if (item.id().isBlank()) return@forEach

        val entity = CommentTreeEntity()
        val topicSubReply = item.select(".topic_sub_reply").remove()
        if (topicSubReply.isNotEmpty()) {
            entity.topicSubReply = topicSubReply.select(".topic_sub_reply > div")
                .mapCommentItems(filterDeleteComment, filterBlockUserComment, blockUsers)
        }
        entity.id = item.attr("id").parseCount().toString()
        entity.emojiParam = item.select(".like_dropdown").parserLikeParam()
        item.select("a.avatar").apply {
            entity.userId = hrefId()
            entity.userAvatar = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        entity.userSign = item.select(".sign").text().removeSuffix(")").removePrefix("(")
        entity.userName = item.select("strong > a").text()
        entity.floor = item.select(".post_actions a.floor-anchor").text()
        entity.replyJs = item.select(".post_actions .action > a.icon").attr("onclick")
        entity.time = item.select(".post_actions small")
            .lastTextNode().trim()
            .removePrefix("-").trim()

        entity.replyContent = item.select(".reply_content > .message")
            .ifEmpty { item.select(".inner > .cmt_sub_content") }
            .html().preHandleHtml()

        // 过滤删除数据
        if (filterDeleteComment && entity.replyContent.contains("删除了回复")) {
            return@forEach
        }

        // 过滤绝交用户数据
        if (filterBlockUserComment && blockUsers.contains(entity.userId)) {
            debugLog { "过滤绝交用户（${entity.id}）评论：" + entity.replyContent }
            return@forEach
        }
        entities.add(entity)
    }
    return entities
}

/**
 * Main
 * - subReply('blog',327295,195250,0,837364,824741,0)
 *
 * Sub
 * - subReply('blog' , 327295, 195202, 195251,   539713, 539713,1)
 *
 * 对应
 * - type,
 * - topic_id,
 * - post_id,
 * - sub_reply_id,
 * - sub_reply_uid,
 * - post_uid,
 * - sub_post_type
 */
fun String.parserReplyParam(): CommentFormEntity.CommentParam {
    val groupValues =
        "\\(\\s*'(.*?)'\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\)".toRegex()
            .find(this)?.groupValues.orEmpty()
    val param = CommentFormEntity.CommentParam()
    param.type = groupValues.getOrNull(1).orEmpty()
    param.topicId = groupValues.getOrNull(2)?.toLongOrNull() ?: 0
    param.postId = groupValues.getOrNull(3)?.toLongOrNull() ?: 0
    param.subReplyId = groupValues.getOrNull(4)?.toLongOrNull() ?: 0
    param.subReplyUid = groupValues.getOrNull(5)?.toLongOrNull() ?: 0
    param.postUid = groupValues.getOrNull(6)?.toLongOrNull() ?: 0
    param.subPostType = groupValues.getOrNull(7)?.toLongOrNull() ?: 0
    return param
}