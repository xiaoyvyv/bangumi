@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.LongDef

/**
 * 用户组定义
 */
object UserGroup {
    const val ADMIN = 1L                // 管理员
    const val BANGUMI_ADMIN = 2L        // Bangumi 管理猿
    const val TIANCHUANG_ADMIN = 3L     // 天窗管理猿
    const val MUTED = 4L                // 禁言用户
    const val BANNED = 5L               // 禁止访问用户
    const val CHARACTER_ADMIN = 8L      // 人物管理猿
    const val WIKI_ENTRY_ADMIN = 9L     // 维基条目管理猿
    const val USER = 10L                // 普通用户
    const val WIKIER = 11L              // 维基人

    @LongDef(
        ADMIN,
        BANGUMI_ADMIN,
        TIANCHUANG_ADMIN,
        MUTED,
        BANNED,
        CHARACTER_ADMIN,
        WIKI_ENTRY_ADMIN,
        USER,
        WIKIER
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Group
}
