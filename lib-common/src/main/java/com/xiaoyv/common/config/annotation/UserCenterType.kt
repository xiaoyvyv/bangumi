package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [UserCenterType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    UserCenterType.TYPE_OVERVIEW,
    UserCenterType.TYPE_SAVE,
    UserCenterType.TYPE_CHART,
    UserCenterType.TYPE_TIMELINE,
    UserCenterType.TYPE_ABOUT
)
@Retention(AnnotationRetention.SOURCE)
annotation class UserCenterType {
    companion object {
        const val TYPE_OVERVIEW = 0
        const val TYPE_SAVE = 1
        const val TYPE_CHART = 2
        const val TYPE_TIMELINE = 3
        const val TYPE_ABOUT = 4
    }
}
