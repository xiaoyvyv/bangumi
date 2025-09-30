package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * - 0 = 条目
 * - 1 = 角色
 * - 2 = 人物
 * - 3 = 剧集
 */
@IntDef(
    IndexCatType.SUBJECT,
    IndexCatType.CHARACTER,
    IndexCatType.PERSON,
    IndexCatType.EP
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexCatType {
    companion object {
        const val SUBJECT = 0
        const val CHARACTER = 1
        const val PERSON = 2
        const val EP = 3
    }
}
