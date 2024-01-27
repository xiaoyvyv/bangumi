package com.xiaoyv.script.database.dao

/**
 * Class: [SubjectDao]
 *
 * @author why
 * @since 1/15/24
 */
data class SubjectDao(
    var id: Long = 0,
    var name: String = "",
    var name_cn: String = "",
    var type: Int = 0,
    var nsfw: Boolean = false,
)
