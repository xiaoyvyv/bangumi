package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param content
 * @param turnstileToken 需要 [turnstile](https://developers.cloudflare.com/turnstile/get-started/client-side-rendering/) next.bgm.tv 域名对应的 site-key 为 `0x4AAAAAAABkMYinukE8nzYS` dev.bgm38.tv 域名使用测试用的 site-key `1x00000000000000000000AA`
 * @param replyTo 被回复的回复 ID, `0` 代表发送顶层回复
 */
@Serializable

data class CreateBlogCommentRequest(

    @SerialName(value = "content") @Required val content: String,

    /* 需要 [turnstile](https://developers.cloudflare.com/turnstile/get-started/client-side-rendering/) next.bgm.tv 域名对应的 site-key 为 `0x4AAAAAAABkMYinukE8nzYS` dev.bgm38.tv 域名使用测试用的 site-key `1x00000000000000000000AA` */
    @SerialName(value = "turnstileToken") @Required val turnstileToken: String,

    /* 被回复的回复 ID, `0` 代表发送顶层回复 */
    @SerialName(value = "replyTo") val replyTo: Int? = 0,

    )
