package com.xiaoyv.bangumi.shared.data.model.emnu

import androidx.annotation.StringDef

/**
 * 目录关联类型
 *
 * - subject = 条目
 * - character = 角色
 * - person = 人物
 * - episode = 剧集
 */
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    IndexRelatedCategory.SUBJECT,
    IndexRelatedCategory.CHARACTER,
    IndexRelatedCategory.PERSON,
    IndexRelatedCategory.EPISODE
)
annotation class IndexRelatedCategory {
    companion object Companion {
        const val SUBJECT = "0"
        const val CHARACTER = "1"
        const val PERSON = "2"
        const val EPISODE = "3"
    }
}