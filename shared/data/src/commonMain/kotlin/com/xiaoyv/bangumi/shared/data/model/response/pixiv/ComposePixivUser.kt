package com.xiaoyv.bangumi.shared.data.model.response.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposePixivUser] Pixiv 用户基本信息
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class ComposePixivUser(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("account") val account: String = "",
    @SerialName("mail_address") val mailAddress: String = "",
    @SerialName("is_premium") val isPremium: Boolean = false,
    @SerialName("x_restrict") val xRestrict: Int = 0,
    @SerialName("is_mail_authorized") val isMailAuthorized: Boolean = false,
    @SerialName("require_policy_agreement") val requirePolicyAgreement: Boolean = false,
    @SerialName("profile_image_urls") val profileImageUrls: ComposePixivProfileImageUrls = ComposePixivProfileImageUrls(),
) {
    companion object {
        val Empty = ComposePixivUser()
    }
}
