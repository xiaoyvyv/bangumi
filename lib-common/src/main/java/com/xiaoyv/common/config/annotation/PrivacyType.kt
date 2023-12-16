package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [PrivacyType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    PrivacyType.TYPE_ALL,
    PrivacyType.TYPE_FRIEND,
    PrivacyType.TYPE_NONE
)
@Retention(AnnotationRetention.SOURCE)
annotation class PrivacyType {
    companion object {
        const val TYPE_ALL = 0
        const val TYPE_FRIEND = 1
        const val TYPE_NONE = 2
    }
}
