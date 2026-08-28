package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * 职业类型常量定义：
 *
 * producer（制作人）、mangaka（漫画家）、artist（美术/画师）、
 * seiyu（声优）、writer（编剧/作家）、illustrator（插画师）、
 * actor（演员）
 */
@StringDef(
    PersonCareerType.PRODUCER,
    PersonCareerType.MANGAKA,
    PersonCareerType.ARTIST,
    PersonCareerType.SEIYU,
    PersonCareerType.WRITER,
    PersonCareerType.ILLUSTRATOR,
    PersonCareerType.ACTOR
)
@Retention(AnnotationRetention.SOURCE)
annotation class PersonCareerType {
    companion object {
        const val PRODUCER = "producer"         // 制作人
        const val MANGAKA = "mangaka"           // 漫画家
        const val ARTIST = "artist"             // 美术 / 画师
        const val SEIYU = "seiyu"               // 声优
        const val WRITER = "writer"             // 编剧 / 作家
        const val ILLUSTRATOR = "illustrator"   // 插画师
        const val ACTOR = "actor"               // 演员

        fun string(type: String): String {
            return when (type) {
                PRODUCER -> "制作人"
                MANGAKA -> "漫画家"
                ARTIST -> "美术"
                SEIYU -> "声优"
                WRITER -> "编剧"
                ILLUSTRATOR -> "插画师"
                ACTOR -> "演员"
                else -> type
            }
        }
    }
}
