package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import com.xiaoyv.common.api.response.UserEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SignUpVerifyEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SignUpVerifyEntity(
    var error: String? = null,
    var success: Boolean = false,
    var message: String = "",
) : Parcelable