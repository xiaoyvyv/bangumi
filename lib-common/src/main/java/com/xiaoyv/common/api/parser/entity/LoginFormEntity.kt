package com.xiaoyv.common.api.parser.entity

/**
 * Class: [LoginFormEntity]
 *
 * @author why
 * @since 11/25/23
 */
data class LoginFormEntity(
    var forms: HashMap<String, String> = hashMapOf(),
    var hasLogin: Boolean = false,
    var loginInfo: LoginResultEntity? = null
)