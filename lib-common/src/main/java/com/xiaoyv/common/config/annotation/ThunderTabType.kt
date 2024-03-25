package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [ThunderTabType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    ThunderTabType.TYPE_DOWNLOADING,
    ThunderTabType.TYPE_DOWNLOADED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ThunderTabType {
    companion object {
        const val TYPE_DOWNLOADING = "downloading"
        const val TYPE_DOWNLOADED = "downloaded"
    }
}
