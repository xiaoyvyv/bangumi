package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.career_actor
import com.xiaoyv.bangumi.core_resource.resources.career_artist
import com.xiaoyv.bangumi.core_resource.resources.career_illustrator
import com.xiaoyv.bangumi.core_resource.resources.career_producer
import com.xiaoyv.bangumi.core_resource.resources.career_seiyu
import com.xiaoyv.bangumi.core_resource.resources.career_writer
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import org.jetbrains.compose.resources.StringResource

/**
 * [CareerType]
 *
 * 职业类型常量定义：
 *
 * producer（制作人）、mangaka（漫画家）、artist（美术/画师）、
 * seiyu（声优）、writer（编剧/作家）、illustrator（插画师）、
 * actor（演员）
 *
 * @since 2025/5/18
 */
@StringDef(
    CareerType.PRODUCER,
    CareerType.MANGAKA,
    CareerType.ARTIST,
    CareerType.SEIYU,
    CareerType.WRITER,
    CareerType.ILLUSTRATOR,
    CareerType.ACTOR
)
@Retention(AnnotationRetention.SOURCE)
annotation class CareerType {
    companion object {
        const val PRODUCER = "producer"         // 制作人
        const val MANGAKA = "mangaka"           // 漫画家
        const val ARTIST = "artist"             // 美术 / 画师
        const val SEIYU = "seiyu"               // 声优
        const val WRITER = "writer"             // 编剧 / 作家
        const val ILLUSTRATOR = "illustrator"   // 插画师
        const val ACTOR = "actor"               // 演员

        fun string(@CareerType type: String): StringResource {
            return when (type) {
                PRODUCER -> Res.string.career_producer
                MANGAKA -> Res.string.career_producer
                ARTIST -> Res.string.career_artist
                SEIYU -> Res.string.career_seiyu
                WRITER -> Res.string.career_writer
                ILLUSTRATOR -> Res.string.career_illustrator
                ACTOR -> Res.string.career_actor
                else -> Res.string.global_unknown
            }
        }
    }
}

