package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * Class: [CollectionWebPath]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    CollectionWebPath.TYPE_WISH,
    CollectionWebPath.TYPE_COLLECT,
    CollectionWebPath.TYPE_DO,
    CollectionWebPath.TYPE_ON_HOLD,
    CollectionWebPath.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectionWebPath {
    companion object Companion {
        const val TYPE_WISH = "wish"
        const val TYPE_COLLECT = "collect"
        const val TYPE_DO = "do"
        const val TYPE_ON_HOLD = "on_hold"
        const val TYPE_DROPPED = "dropped"

        fun from(@CollectionType type: Int): String {
            return when (type) {
                CollectionType.WISH -> TYPE_WISH
                CollectionType.DONE -> TYPE_COLLECT
                CollectionType.DOING -> TYPE_DO
                CollectionType.ASIDE -> TYPE_ON_HOLD
                CollectionType.DROP -> TYPE_DROPPED
                else -> TYPE_WISH
            }
        }
    }
}
