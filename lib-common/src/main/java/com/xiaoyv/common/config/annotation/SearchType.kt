package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [SearchType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    SearchType.TYPE_MEDIA,
    SearchType.TYPE_MONO,
    SearchType.TYPE_INDEX,
    SearchType.TYPE_TAG,
    SearchType.TYPE_TOPIC
)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchType {
    companion object {
        const val TYPE_MEDIA = "media"
        const val TYPE_MONO = "mono"
        const val TYPE_TAG = "tag"
        const val TYPE_TOPIC = "topic"
        const val TYPE_INDEX = "index"
    }
}
