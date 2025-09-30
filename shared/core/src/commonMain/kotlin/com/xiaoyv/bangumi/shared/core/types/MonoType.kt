package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [MonoType]
 *
 * @since 2025/5/18
 */
@IntDef(
    MonoType.UNKNOWN,
    MonoType.PERSON,
    MonoType.CHARACTER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MonoType {
    companion object {
        const val UNKNOWN = 0
        const val PERSON = 1
        const val CHARACTER = 2
    }
}
