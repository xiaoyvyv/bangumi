package com.xiaoyv.bangumi.shared.data.model.request

import kotlinx.serialization.SerialName

/**
 * @param content bbcode
 * @param title
 * @param turnstileToken 需要 [turnstile](https://developers.cloudflare.com/turnstile/get-started/client-side-rendering/) next.bgm.tv 域名对应的 site-key 为 `0x4AAAAAAABkMYinukE8nzYS` dev.bgm38.tv 域名使用测试用的 site-key `1x00000000000000000000AA`
 */
data class CreateGroupTopicRequest(

    /* bbcode */
    @SerialName("content")
    val content: String,

    @SerialName("title")
    val title: String,

    /* 需要 [turnstile](https://developers.cloudflare.com/turnstile/get-started/client-side-rendering/) next.bgm.tv 域名对应的 site-key 为 `0x4AAAAAAABkMYinukE8nzYS` dev.bgm38.tv 域名使用测试用的 site-key `1x00000000000000000000AA` */
    @SerialName("turnstileToken")
    val turnstileToken: String,
)
