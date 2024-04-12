package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import com.xiaoyv.common.api.response.UserEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SignUpResultEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SignUpResultEntity(
    var error: String? = null,
    var success: Boolean = false,
    var message: String = "",
    var forms: HashMap<String, String> = hashMapOf()
) : Parcelable