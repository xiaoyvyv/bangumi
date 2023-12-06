package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [PersonBinderClickType]
 *
 * @author why
 * @since 12/6/23
 */
@IntDef(
    PersonBinderClickType.TYPE_PERSON_REAL,
    PersonBinderClickType.TYPE_PERSON_VIRTUAL,
    PersonBinderClickType.TYPE_USER,
    PersonBinderClickType.TYPE_INDEX,
    PersonBinderClickType.TYPE_OPUS,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PersonBinderClickType {
    companion object {
        const val TYPE_USER = 0
        const val TYPE_PERSON_REAL = 1
        const val TYPE_PERSON_VIRTUAL = 2
        const val TYPE_INDEX = 3
        const val TYPE_OPUS = 6
    }
}
