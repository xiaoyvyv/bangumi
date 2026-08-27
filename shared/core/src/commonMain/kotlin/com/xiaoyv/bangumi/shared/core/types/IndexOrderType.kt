package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * [IndexOrderType]
 *
 * @author why
 * @since 2025/1/16
 */
@StringDef(
    IndexOrderType.HOT,
    IndexOrderType.LATEST,
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexOrderType {
    companion object {
        const val HOT = "hot"
        const val LATEST = "latest"
    }
}
