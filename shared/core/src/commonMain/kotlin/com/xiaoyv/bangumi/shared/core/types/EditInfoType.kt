package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_avatar
import com.xiaoyv.bangumi.core_resource.resources.global_github
import com.xiaoyv.bangumi.core_resource.resources.global_ins
import com.xiaoyv.bangumi.core_resource.resources.global_intro
import com.xiaoyv.bangumi.core_resource.resources.global_nickname
import com.xiaoyv.bangumi.core_resource.resources.global_not_set
import com.xiaoyv.bangumi.core_resource.resources.global_pixiv
import com.xiaoyv.bangumi.core_resource.resources.global_psn
import com.xiaoyv.bangumi.core_resource.resources.global_sign
import com.xiaoyv.bangumi.core_resource.resources.global_steam
import com.xiaoyv.bangumi.core_resource.resources.global_timezone
import com.xiaoyv.bangumi.core_resource.resources.global_twitter
import com.xiaoyv.bangumi.core_resource.resources.global_user_site
import com.xiaoyv.bangumi.core_resource.resources.global_xbox
import org.jetbrains.compose.resources.StringResource

/**
 * [EditInfoType]
 *
 * @author why
 * @since 2025/1/16
 */
@StringDef(
    EditInfoType.TYPE_FORM_HASH,
    EditInfoType.TYPE_AVATAR,
    EditInfoType.TYPE_NICKNAME,
    EditInfoType.TYPE_SIGN,
    EditInfoType.TYPE_TIMEZONE,
    EditInfoType.TYPE_SITE,
    EditInfoType.TYPE_INTRO,
    EditInfoType.TYPE_INTERNET_PSN,
    EditInfoType.TYPE_INTERNET_XBOX,
    EditInfoType.TYPE_INTERNET_STEAM,
    EditInfoType.TYPE_INTERNET_PIXI,
    EditInfoType.TYPE_INTERNET_GITHUB,
    EditInfoType.TYPE_INTERNET_TWITTER,
    EditInfoType.TYPE_INTERNET_INS,
    EditInfoType.TYPE_SUBMIT,
)
@Retention(AnnotationRetention.SOURCE)
annotation class EditInfoType {
    companion object {
        const val TYPE_FORM_HASH = "formhash"
        const val TYPE_AVATAR = "picfile"
        const val TYPE_NICKNAME = "nickname"
        const val TYPE_SIGN = "sign_input"
        const val TYPE_TIMEZONE = "timeoffsetnew"
        const val TYPE_SITE = "newsite"
        const val TYPE_INTRO = "newbio"
        const val TYPE_INTERNET_PSN = "network_service[1]"
        const val TYPE_INTERNET_XBOX = "network_service[2]"
        const val TYPE_INTERNET_STEAM = "network_service[4]"
        const val TYPE_INTERNET_PIXI = "network_service[6]"
        const val TYPE_INTERNET_GITHUB = "network_service[7]"
        const val TYPE_INTERNET_TWITTER = "network_service[8]"
        const val TYPE_INTERNET_INS = "network_service[9]"
        const val TYPE_SUBMIT = "submit"

        fun string(@EditInfoType type: String): StringResource {
            return when (type) {
                TYPE_AVATAR -> Res.string.global_avatar
                TYPE_NICKNAME -> Res.string.global_nickname
                TYPE_SIGN -> Res.string.global_sign
                TYPE_TIMEZONE -> Res.string.global_timezone
                TYPE_SITE -> Res.string.global_user_site
                TYPE_INTRO -> Res.string.global_intro
                TYPE_INTERNET_PSN -> Res.string.global_psn
                TYPE_INTERNET_XBOX -> Res.string.global_xbox
                TYPE_INTERNET_STEAM -> Res.string.global_steam
                TYPE_INTERNET_PIXI -> Res.string.global_pixiv
                TYPE_INTERNET_GITHUB -> Res.string.global_github
                TYPE_INTERNET_TWITTER -> Res.string.global_twitter
                TYPE_INTERNET_INS -> Res.string.global_ins
                else -> Res.string.global_not_set
            }
        }
    }
}
