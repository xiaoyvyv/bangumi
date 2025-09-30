package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [CommentFilterType]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    CommentFilterType.ALL,
    CommentFilterType.REACTION,
    CommentFilterType.MASTER,
    CommentFilterType.FRIEND,
    CommentFilterType.SELF,
)
@Retention(AnnotationRetention.SOURCE)
annotation class CommentFilterType {
    companion object {
        const val ALL = 0
        const val REACTION = 1
        const val MASTER = 2
        const val FRIEND = 3
        const val SELF = 4
    }
}
