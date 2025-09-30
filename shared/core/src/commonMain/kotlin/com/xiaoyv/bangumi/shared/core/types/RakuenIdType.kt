@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_topic_blog
import com.xiaoyv.bangumi.core_resource.resources.type_topic_crt
import com.xiaoyv.bangumi.core_resource.resources.type_topic_ep
import com.xiaoyv.bangumi.core_resource.resources.type_topic_group
import com.xiaoyv.bangumi.core_resource.resources.type_topic_index
import com.xiaoyv.bangumi.core_resource.resources.type_topic_person
import com.xiaoyv.bangumi.core_resource.resources.type_topic_subject
import com.xiaoyv.bangumi.core_resource.resources.type_unknown
import org.jetbrains.compose.resources.StringResource

/**
 * Class: [RakuenIdType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    RakuenIdType.TYPE_UNKNOWN,
    RakuenIdType.TYPE_EP,
    RakuenIdType.TYPE_GROUP,
    RakuenIdType.TYPE_PERSON,
    RakuenIdType.TYPE_CRT,
    RakuenIdType.TYPE_SUBJECT,
    RakuenIdType.TYPE_INDEX,
    RakuenIdType.TYPE_BLOG,
)
@Retention(AnnotationRetention.SOURCE)
annotation class RakuenIdType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_EP = "ep"
        const val TYPE_GROUP = "group"
        const val TYPE_PERSON = "prsn"
        const val TYPE_CRT = "crt"
        const val TYPE_SUBJECT = "subject"
        const val TYPE_INDEX = "index"
        const val TYPE_BLOG = "blog"

        fun isSupportRection(@RakuenIdType type: String): Boolean {
            return type == TYPE_BLOG || type == TYPE_GROUP || type == TYPE_SUBJECT
        }

        fun string(@RakuenIdType type: String): StringResource {
            return when (type) {
                TYPE_EP -> Res.string.type_topic_ep
                TYPE_GROUP -> Res.string.type_topic_group
                TYPE_PERSON -> Res.string.type_topic_person
                TYPE_CRT -> Res.string.type_topic_crt
                TYPE_SUBJECT -> Res.string.type_topic_subject
                TYPE_INDEX -> Res.string.type_topic_index
                TYPE_BLOG -> Res.string.type_topic_blog
                else -> Res.string.type_unknown
            }
        }
    }
}