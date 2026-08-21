package com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement


@Immutable
@Serializable
data class ComposeReaction(
    @SerialName("value") val value: String = "",
    @SerialName("users") val users: SerializeList<ComposeUser> = persistentListOf(),


    @SerialName("type") val type: Int = 0,
    @SerialName("main_id") val mainId: Long = 0,
    @SerialName("total") val total: Int = 0,
    @SerialName("emoji") val emoji: String = "",
) {
    val count get() = if (total != 0) total else users.size

    /**
     * 自己是否点过
     */
    val selected: Boolean
        @Composable
        get() {
            val self = currentUser()
            return remember(users, self) {
                users.any { it.id == self.id }
            }
        }

    companion object {
        /**
         * 解析贴贴表情数据
         */
        fun fromJson(json: String): PersistentMap<String, SerializeList<ComposeReaction>> {
            val element = defaultJson.parseToJsonElement(json)
            if (element !is JsonObject) return persistentMapOf()
            val result = mutableMapOf<String, SerializeList<ComposeReaction>>()
            for ((key, value) in element) {
                val reactions: List<ComposeReaction> = when (value) {
                    is JsonArray -> value.map { defaultJson.decodeFromJsonElement<ComposeReaction>(it) }
                    is JsonObject -> value.values.map { defaultJson.decodeFromJsonElement<ComposeReaction>(it) }
                    else -> emptyList()
                }
                result[key] = reactions.toPersistentList()
            }
            return result.toPersistentMap()
        }
    }
}


fun ComposeReply.refreshReaction(
    userManager: UserManager,
    commentId: Long,
    reaction: ComposeReaction,
): ComposeReply {
    val self = userManager.userInfo.username
    val isLiked = reaction.users.any { it.username == userManager.userInfo.username }

    val updatedReplies = if (replies.isEmpty()) {
        replies
    } else {
        replies.refreshReaction(userManager, commentId, reaction)
    }

    return if (id == commentId) {
        // 先从全部的贴贴移除自己
        val reactionsList = reactions
            .map { it.copy(users = it.users.filter { user -> user.username != self }.toImmutableList()) }
            .toMutableList()

        // 评论没有该贴贴直接添加一个
        val newReactions = if (reactionsList.find { it.value == reaction.value } == null) {
            reactionsList.add(reaction.copy(users = persistentListOf(userManager.userInfo)))
            reactionsList
        } else {
            // 添加
            if (!isLiked) {
                reactionsList.map {
                    if (it.value == reaction.value) {
                        val users = it.users.toMutableList()
                        users.add(userManager.userInfo)
                        it.copy(users = users.toImmutableList())
                    } else {
                        it
                    }
                }
            } else {
                reactionsList
            }
        }

        copy(
            replies = updatedReplies,
            reactions = newReactions.filter { it.users.isNotEmpty() }.toImmutableList()
        )
    } else {
        copy(replies = updatedReplies)
    }
}

fun List<ComposeReply>.refreshReaction(
    userManager: UserManager,
    commentId: Long,
    reaction: ComposeReaction,
): ImmutableList<ComposeReply> {
    return map { it.refreshReaction(userManager, commentId, reaction) }.toImmutableList()
}
