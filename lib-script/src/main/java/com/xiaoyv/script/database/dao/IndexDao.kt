package com.xiaoyv.script.database.dao

/**
 * Class: [IndexDao]
 *
 * @author why
 * @since 1/15/24
 */
data class IndexDao(
    var id: Long = 0,
    var desc: String? = null,
    var title: String? = null,
    var nickname: String? = null,
    var username: String? = null,
    var ban: Int = 0,
    var nsfw: Int = 0,
    var total: Int = 0,
    var collects: Int = 0,
    var comments: Int = 0,
    var createdAt: Long = 0,
    var updatedAt: Long = 0,
    var error: String? = null,
)
