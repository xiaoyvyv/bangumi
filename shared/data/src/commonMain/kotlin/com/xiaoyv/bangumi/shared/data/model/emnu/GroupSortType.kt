package com.xiaoyv.bangumi.shared.data.model.emnu


import androidx.annotation.StringDef

/**
 * 小组排序方式
 *
 * - posts = 帖子数
 * - topics = 主题数
 * - members = 成员数
 * - created = 创建时间
 * - updated = 最新讨论
 *
 * Values: posts,topics,members,created,updated
 */
@StringDef(
    GroupSortType.POSTS,
    GroupSortType.TOPICS,
    GroupSortType.MEMBERS,
    GroupSortType.CREATED,
    GroupSortType.UPDATED
)
@Retention(AnnotationRetention.SOURCE)
annotation class GroupSortType {
    companion object Companion {
        /**
         * 这两个没效果，暂时隐藏
         */
        const val POSTS = "posts"
        const val UPDATED = "updated"

        const val TOPICS = "topics"
        const val MEMBERS = "members"
        const val CREATED = "created"
    }
}


