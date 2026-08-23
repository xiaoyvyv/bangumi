@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

/**
 * [ListIllustType] 插画列表展示类型
 *
 * - [UNKNOWN]: 未知类型
 * - [RANK]: 排行榜
 * - [USER]: 用户作品列表
 * - [SEARCH]: 插画搜索列表
 *
 * @author why
 * @since 2025/1/25
 */
@IntDef(
    ListIllustType.UNKNOWN,
    ListIllustType.RANK,
    ListIllustType.USER,
    ListIllustType.SEARCH,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListIllustType {
    companion object Companion {
        // 未知类型
        const val UNKNOWN = 0

        // 排行榜
        const val RANK = 1

        // 用户作品列表
        const val USER = 2

        // 插画搜索列表
        const val SEARCH = 3
    }
}
