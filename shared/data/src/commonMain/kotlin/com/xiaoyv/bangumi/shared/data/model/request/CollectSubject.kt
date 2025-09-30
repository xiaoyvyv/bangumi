package com.xiaoyv.bangumi.shared.data.model.request


import com.xiaoyv.bangumi.shared.core.types.CollectionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param comment 评价
 * @param private 仅自己可见
 * @param progress 是否自动完成条目进度，仅在 `type` 为 `看过` 时有效，并且不会产生对应的时间线记录：           - 书籍条目会检查总的话数和卷数，并更新收藏进度到最新;           - 动画和三次元会标记所有正片章节为已完成，并同时更新收藏进度
 * @param rate 评分，0 表示删除评分
 * @param tags
 * @param type
 */
@Serializable

data class CollectSubject(

    /* 评价 */
    @SerialName(value = "comment") val comment: String? = null,

    /* 仅自己可见 */
    @SerialName(value = "private") val `private`: Boolean? = null,

    /* 是否自动完成条目进度，仅在 `type` 为 `看过` 时有效，并且不会产生对应的时间线记录：           - 书籍条目会检查总的话数和卷数，并更新收藏进度到最新;           - 动画和三次元会标记所有正片章节为已完成，并同时更新收藏进度 */
    @SerialName(value = "progress") val progress: Boolean? = null,

    /* 评分，0 表示删除评分 */
    @SerialName(value = "rate") val rate: Int? = null,

    @SerialName(value = "tags") val tags: List<String>? = null,

    @SerialName(value = "type") @field:CollectionType val type: Int? = null,
)

