package com.xiaoyv.common.api.parser.entity

import com.xiaoyv.common.api.response.UserEntity

/**
 * Class: [LoginResultEntity]
 *
 * @author why
 * @since 11/25/23
 */
data class LoginResultEntity(
    var error: String? = null,
    var success: Boolean = false,
    var message: String = "",
    var userEntity: UserEntity = UserEntity(isEmpty = true)
)