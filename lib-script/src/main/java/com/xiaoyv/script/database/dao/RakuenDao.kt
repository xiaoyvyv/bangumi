package com.xiaoyv.script.database.dao

/**
 * Class: [RakuenDao]
 *
 * @author why
 * @since 1/14/24
 */
data class RakuenDao(
    val id: Long = 0,
    val title: String = "",
    val summary: String = "",
    val uid: String = "",
    val user_name: String = "",
    val user_avatar: String = "",
    val image: String = "",
    val time: String = "",
    val bgm_grp: String = "",
    val bgm_grp_name: String = "",
    val bgm_grp_avatar: String = "",
    val error: String? = null,
)

