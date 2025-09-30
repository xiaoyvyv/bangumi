package com.xiaoyv.bangumi.shared.data.model.request

import androidx.annotation.StringDef

/**
 * 小组话题过滤模式
 *
 * - all = 全站
 * - joined = 已加入
 * - created = 我创建的
 * - replied = 我回复的
 */
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    GroupTopicFilter.ALL,
    GroupTopicFilter.JOINED,
    GroupTopicFilter.CREATED,
    GroupTopicFilter.REPLIED
)
annotation class GroupTopicFilter {
    companion object {
        const val ALL = "all"
        const val JOINED = "joined"
        const val CREATED = "created"
        const val REPLIED = "replied"
    }
}