package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.lastTextNode
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.toJson
import com.xiaoyv.bangumi.shared.core.utils.toLongValue
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCommentSubParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@AppParserDsl
class CommentParser : BaseParser() {
    /**
     * 解析话题的评论
     */
    fun Element.parserBottomComment(@CommentType type: Int): List<ComposeComment> {
        // 解析 gh
        val filterDeleteComment = false
        val filterBreakUpComment = false
        val blockUsers = listOf<String>()

        debugLog { "绝交用户过滤：${filterBreakUpComment}：" + blockUsers.toJson() }

        // 解析评论
        // 帖子下方评论: #comment_list > div
        // 人物、条目下方评论: #comment_box > div
        return select("#comment_list > div, #comment_box > div").mapCommentItems(
            type = type,
            filterDeleteComment = filterDeleteComment,
            filterBlockUserComment = filterBreakUpComment,
            blockUsers = blockUsers,
        )
    }


    /**
     * 解析评论
     *
     * @param filterDeleteComment 是否过滤删除的评论
     * @param filterBlockUserComment 是否过滤屏蔽用户评论
     * @param blockUsers 屏蔽的用户
     */
    private fun Elements.mapCommentItems(
        @CommentType type: Int,
        filterDeleteComment: Boolean,
        filterBlockUserComment: Boolean,
        blockUsers: List<String>,
        parent: ComposeComment? = null,
    ): List<ComposeComment> {
        val entities = arrayListOf<ComposeComment>()

        forEach { item ->
            // 这里优先判断是否有子评论，并从当前节点移除，避免解析重复数据
            var replies = persistentListOf<ComposeComment>()
            val topicSubReply = item.select(".topic_sub_reply").remove()

            // 如果没有评论ID，则解析 data-item-user，这种情况通常是条目底部的吐槽
            val id = item.id().substringAfter("_").ifBlank { item.attr("data-item-user") }
            val message = item.select(".reply_content > .message")
                .ifEmpty { item.select(".inner > .cmt_sub_content") }

            val replyQuote = message.select("div.quote").remove().text().take(100)
            val content = message.html()
            val replyJs = item.select(".post_actions .action > a.icon").attr("onclick")

            val comment = ComposeComment(
                id = id,
                type = type,
                emojiParam = item.select(".like_dropdown").parserLikeParam(),
                time = item.select(".post_actions small")
                    .lastTextNode().trim()
                    .removePrefix("-").trim(),
                comment = content,
                commentHtml = content.parseAsHtml(),
                replyParam = replyJs.parserReplyParam(),
                replyQuote = replyQuote,
                floor = item.select(".post_actions a.floor-anchor").text(),
                user = ComposeUser(
                    nickname = item.select("strong > a").text(),
                    username = item.select("a.avatar").hrefId(),
                    avatar = ComposeImages.fromUrl(
                        item.select("a.avatar span").styleAvatarUrl()
                    ),
                    sign = item.select(".sign").text()
                        .removeSuffix(")")
                        .removePrefix("(")
                ),
                parent = parent
            )

            if (topicSubReply.isNotEmpty()) {
                replies = topicSubReply.select(".topic_sub_reply > div")
                    .mapCommentItems(type, filterDeleteComment, filterBlockUserComment, blockUsers, comment)
                    .toPersistentList()
            }

            entities.add(comment.copy(children = replies))
        }
        return entities.toImmutableList()
    }

    /**
     * 解析发回复表单
     */
    fun Element.parserReplyForm(): ComposeCommentParam {
        return ComposeCommentParam(
            action = select("#ReplyForm").attr("action"),
            inputs = select("#ReplyForm input").associate {
                it.attr("name") to it.attr("value")
            }
        )
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
    fun String.parserReplyParam(): ComposeCommentSubParam {
        val groupValues =
            "\\(\\s*'(.*?)'\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\s*,\\s*(.*?)\\)".toRegex()
                .find(this)?.groupValues.orEmpty()
        return ComposeCommentSubParam(
            type = groupValues.getOrNull(1).orEmpty(),
            topicId = groupValues.getOrNull(2).toLongValue(),
            postId = groupValues.getOrNull(3).toLongValue(),
            subReplyId = groupValues.getOrNull(4).toLongValue(),
            subReplyUid = groupValues.getOrNull(5).toLongValue(),
            postUid = groupValues.getOrNull(6).toLongValue(),
            subPostType = groupValues.getOrNull(7).toLongValue(),
        )
    }
}