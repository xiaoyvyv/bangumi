package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import com.xiaoyv.common.api.response.UserEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SignInResultEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SignInResultEntity(
    var error: String? = null,
    var success: Boolean = false,
    var message: String = "",
    var userEntity: UserEntity = UserEntity(isEmpty = true)
) : Parcelable