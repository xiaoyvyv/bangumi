package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.response.base.BgmActionResponse
import com.xiaoyv.common.kts.fromJson
import kotlinx.parcelize.Parcelize


/**
 * Class: [LikeEntity]
 *
 * ```
 * {
 *   "2273678": {
 *     "132": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "132",
 *       "total": 2,
 *       "emoji": "93",
 *       "users": [
 *         {
 *           "username": "777600",
 *           "nickname": "hoshino"
 *         },
 *         {
 *           "username": "missminmay",
 *           "nickname": "envys"
 *         }
 *       ]
 *     }
 *   }
 *   "2273678": [
 *     {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "132",
 *       "total": 2,
 *       "emoji": "93",
 *       "users": [
 *         {
 *           "username": "777600",
 *           "nickname": "hoshino"
 *         },
 *         {
 *           "username": "missminmay",
 *           "nickname": "envys"
 *         }
 *       ]
 *    }
 *  ]
 * ```
 *
 * @author why
 * @since 12/2/23
 */
@Keep
@Parcelize
data class LikeEntity(val i: Int = 10) : HashMap<String, Any>(i), Parcelable {

    @Keep
    @Parcelize
    data class LikeAction(
        @SerializedName("emoji")
        var emoji: String? = "",
        @SerializedName("main_id")
        var mainId: Int = 0,
        @SerializedName("total")
        var total: Int = 0,
        @SerializedName("type")
        var type: Int = 0,
        @SerializedName("users")
        var users: List<User>? = listOf(),
        @SerializedName("value")
        var value: String? = "",
        @SerializedName("selected")
        var selected: Boolean = false,
    ) : Parcelable

    @Keep
    @Parcelize
    data class User(
        @SerializedName("nickname")
        var nickname: String? = null,
        @SerializedName("username")
        var username: String? = null,
    ) : Parcelable

    companion object {
        /**
         * 优化贴贴数据结构
         */
        fun BgmActionResponse<LikeEntity>.normal(commentId: String): MutableMap<String, List<LikeAction>> {
            return apply { require(isOk) }.data.normal(commentId)
        }

        /**
         * 优化贴贴数据结构，将 LikeEntity 的 Value 统一为 Map<CommentId -> List<LikeAction>> 结构
         */
        fun LikeEntity?.normal(commentId: String? = null): MutableMap<String, List<LikeAction>> {
            val emojiMap = mutableMapOf<String, List<LikeAction>>()
            orEmpty().forEach { (commentId, mapOrList) ->
                val emojiList = when (mapOrList) {
                    is LinkedTreeMap<*, *> -> mapOrList.values.filterIsInstance(LinkedTreeMap::class.java)
                    is ArrayList<*> -> mapOrList.filterIsInstance(LinkedTreeMap::class.java)
                    else -> emptyList()
                }

                // 将 List<LinkedTreeMap> 转为 List<LikeAction>
                emojiMap[commentId] = emojiList.toJson().fromJson<List<LikeAction>>().orEmpty()
            }

            // 没有 Emoji 时且 commentId 不为空时，手动构建一个空的 Emoji列表传给 H5，防止取消最后一个贴贴后贴贴不消失
            if (emojiMap.isEmpty() && commentId != null) {
                emojiMap[commentId] = emptyList()
            }
            return emojiMap
        }
    }
}