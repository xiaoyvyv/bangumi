@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_topic_crt
import com.xiaoyv.bangumi.core_resource.resources.type_topic_ep
import com.xiaoyv.bangumi.core_resource.resources.type_topic_group
import com.xiaoyv.bangumi.core_resource.resources.type_topic_person
import com.xiaoyv.bangumi.core_resource.resources.type_topic_subject
import com.xiaoyv.bangumi.core_resource.resources.type_unknown
import org.jetbrains.compose.resources.StringResource

/**
 * Class: [RakuenType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    RakuenType.ALL,
    RakuenType.GROUP,
    RakuenType.MY_GROUP,
    RakuenType.SUBJECT,
    RakuenType.EP,
    RakuenType.CHARACTER,
    RakuenType.PERSON,
)
@Retention(AnnotationRetention.SOURCE)
annotation class RakuenType {
    companion object Companion {
        const val ALL = "all"
        const val GROUP = "group"
        const val MY_GROUP = "my_group"
        const val SUBJECT = "subject"
        const val EP = "episode"
        const val CHARACTER = "character"
        const val PERSON = "person"

        fun string(@RakuenType type: String): StringResource {
            return when (type) {
                EP -> Res.string.type_topic_ep
                GROUP -> Res.string.type_topic_group
                MY_GROUP -> Res.string.type_topic_group
                PERSON -> Res.string.type_topic_person
                CHARACTER -> Res.string.type_topic_crt
                SUBJECT -> Res.string.type_topic_subject
                else -> Res.string.type_unknown
            }
        }
    }
}