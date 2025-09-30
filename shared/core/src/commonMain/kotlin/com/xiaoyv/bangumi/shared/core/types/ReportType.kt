@file:Suppress("unused", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * Class: [ReportType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    ReportType.UNKNOWN,
    ReportType.USER,
    ReportType.GROUP_ARTICLE,
    ReportType.GROUP_ARTICLE_COMMENT,
    ReportType.SUBJECT_ARTICLE,
    ReportType.SUBJECT_ARTICLE_COMMENT,
    ReportType.EP_COMMENT,
    ReportType.MONO_COMMENT,
    ReportType.BLOG_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType {
    companion object {
        const val UNKNOWN = 0
        const val USER = 6
        const val GROUP_ARTICLE = 7
        const val GROUP_ARTICLE_COMMENT = 8
        const val SUBJECT_ARTICLE = 9
        const val SUBJECT_ARTICLE_COMMENT = 10
        const val EP_COMMENT = 11
        const val MONO_COMMENT = 12
        const val BLOG_COMMENT = 15
    }
}

