package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * [SearchType]
 *
 * @author why
 * @since 2025/1/16
 */
@StringDef(
    SearchType.SUBJECT,
    SearchType.PERSON,
    SearchType.CHARACTER,
    SearchType.INDEX,
    SearchType.TOPIC,
    SearchType.TAG,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchType {
    companion object {
        const val SUBJECT = "subject"
        const val PERSON = "person"
        const val CHARACTER = "characters"
        const val INDEX = "index"
        const val TOPIC = "topic"
        const val TAG = "tag"
    }
}
