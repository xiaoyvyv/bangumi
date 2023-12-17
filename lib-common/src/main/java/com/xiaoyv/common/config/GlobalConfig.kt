package com.xiaoyv.common.config

import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaTab

/**
 * Class: [GlobalConfig]
 *
 * @author why
 * @since 11/28/23
 */
object GlobalConfig {
    const val GROUP_MY_REPLY_TOPIC = "my_reply"
    const val GROUP_MY_SEND_TOPIC = "my_topic"

    const val DOC_PRIVACY =
        "https://xiaoyvyv.github.io/Bangumi-for-Android/lib-doc/build/starter.html"
    const val DOC_AUTHOR =
        "https://xiaoyvyv.github.io/Bangumi-for-Android/lib-doc/build/author.html"
    const val DOC_DONATION =
        "https://xiaoyvyv.github.io/Bangumi-for-Android/lib-doc/build/donation.html"

    val mediaTypes by lazy {
        listOf(
            MediaTab("动漫", MediaType.TYPE_ANIME),
            MediaTab("书籍", MediaType.TYPE_BOOK),
            MediaTab("音乐", MediaType.TYPE_MUSIC),
            MediaTab("游戏", MediaType.TYPE_GAME),
            MediaTab("三次元", MediaType.TYPE_REAL),
        )
    }

    fun mediaTypeName(@MediaType mediaType: String): String {
        return mediaTypes.find { it.type == mediaType }?.title.orEmpty()
    }
}