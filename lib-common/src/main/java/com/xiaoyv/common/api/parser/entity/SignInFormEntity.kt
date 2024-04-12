package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class: [SignInFormEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Parcelize
data class SignInFormEntity(
    var forms: HashMap<String, String> = hashMapOf(),
    var hasLogin: Boolean = false,
    var loginInfo: SignInResultEntity? = null
) : Parcelable