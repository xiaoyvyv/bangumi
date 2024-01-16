package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [SearchApiType]
 *
 * @author why
 * @since 12/10/23
 */
@StringDef(
    SearchApiType.TYPE_INDEX,
    SearchApiType.TYPE_GROUP_TOPIC,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchApiType {
    companion object {
        const val TYPE_INDEX = "index"
        const val TYPE_GROUP_TOPIC = "group-topic"
    }
}