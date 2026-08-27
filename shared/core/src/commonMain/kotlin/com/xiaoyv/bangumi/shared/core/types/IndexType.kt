package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [IndexType]
 *
 * 目录类型
 * - 0 = 用户
 * - 1 = 公共
 * - 2 = TBA
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    IndexType.USER,
    IndexType.PUBLIC,
    IndexType.AWARD,
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexType {
    companion object {
        const val USER = 0
        const val PUBLIC = 1
        const val AWARD = 2
    }
}
