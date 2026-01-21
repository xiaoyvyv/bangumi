package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.fleeksoft.ksoup.Ksoup
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.serialization.ImmutableMapSerializer
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.core.utils.toLongValue
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * {
 *   "posts": {
 *     "sub": {
 *       "3447806": [
 *         {
 *           "pst_id": "3448894",
 *           "pst_mid": "433483",
 *           "pst_uid": "837364",
 *           "pst_content": "你是好人",
 *           "username": "xiaoyvyv",
 *           "nickname": "小玉",
 *           "sign": "",
 *           "avatar": "//lain.bgm.tv/pic/user/l/000/83/73/837364_zr1p9.jpg?r=1755273891&hd=1",
 *           "dateline": "2025-8-28 19:18",
 *           "model": "group",
 *           "is_self": true
 *         }
 *       ]
 *     }
 *   },
 *   "timestamp": 1756379908,
 *   "status": "ok"
 * }
 */
@Immutable
@Serializable
data class ComposeNewReply(
    @SerialName("posts") val posts: Posts = Posts(),
    @SerialName("status") val status: String = "",
    @SerialName("timestamp") val timestamp: Long = 0,
)

@Immutable
@Serializable
data class Posts(
    @SerialName("main")
    @Serializable(with = ImmutableMapSerializer::class)
    val main: SerializeMap<String, ComposeNewReplyItem> = persistentMapOf(),
    @SerialName("sub")
    @Serializable(with = ImmutableMapSerializer::class)
    val sub: SerializeMap<String, List<ComposeNewReplyItem>> = persistentMapOf(),
)

@Immutable
@Serializable
data class ComposeNewReplyItem(
    @SerialName("avatar") val avatar: String = "",
    @SerialName("dateline") val dateline: String = "",
    @SerialName("is_self") val isSelf: Boolean = false,
    @SerialName("model") val model: String = "",
    @SerialName("nickname") val nickname: String = "",
    @SerialName("pst_content") val pstContent: String = "",
    @SerialName("pst_id") val pstId: String = "",
    @SerialName("pst_mid") val pstMid: String = "",
    @SerialName("pst_uid") val pstUid: String = "",
    @SerialName("sign") val sign: String = "",
    @SerialName("username") val username: String = "",
) {
    /**
     * 将评论响应数据转为 ComposeUI 渲染的数据模型
     */
    fun toComposeComment(
        parent: ComposeComment? = null,
        @CommentType commentType: Int,
        floor: String = "",
    ): ComposeComment {
        val subType = if (parent != null) 1L else 0L
        val subReplyId = if (parent != null) pstId.toLongValue() else 0
        val subReplyUid = pstUid.toLongValue()

        // 处理评论数据
        val message = Ksoup.parse(pstContent)
        val replyQuote = message.select("div.quote").remove().text().take(100)
        val content = message.html()

        return ComposeComment(
            id = pstId,
            type = commentType,
            user = ComposeUser(
                id = subReplyUid,
                username = username,
                nickname = nickname,
                avatar = ComposeImages.fromUrl(avatar.sanitizeImageUrl()),
                sign = sign,
            ),
            comment = content,
            commentHtml = content.parseAsHtml(),
            replyQuote = replyQuote,
            replyParam = ComposeCommentSubParam(
                type = model,
                topicId = pstMid.toLongValue(),
                postId = parent?.id?.toLongValue() ?: pstId.toLongValue(),
                postUid = parent?.user?.id ?: pstUid.toLongValue(),
                subReplyId = subReplyId,
                subReplyUid = subReplyUid,
                subPostType = subType
            ),
            parent = parent,
            time = dateline,
            floor = floor,
        )
    }
}
