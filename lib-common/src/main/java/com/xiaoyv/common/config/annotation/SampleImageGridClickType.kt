package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [SampleImageGridClickType]
 *
 * @author why
 * @since 12/6/23
 */
@IntDef(
    SampleImageGridClickType.TYPE_PERSON_REAL,
    SampleImageGridClickType.TYPE_PERSON_VIRTUAL,
    SampleImageGridClickType.TYPE_USER,
    SampleImageGridClickType.TYPE_INDEX,
    SampleImageGridClickType.TYPE_OPUS,
    SampleImageGridClickType.TYPE_GROUP,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SampleImageGridClickType {
    companion object {
        const val TYPE_USER = 0
        const val TYPE_PERSON_REAL = 1
        const val TYPE_PERSON_VIRTUAL = 2
        const val TYPE_INDEX = 3
        const val TYPE_OPUS = 4
        const val TYPE_GROUP = 5
    }
}
