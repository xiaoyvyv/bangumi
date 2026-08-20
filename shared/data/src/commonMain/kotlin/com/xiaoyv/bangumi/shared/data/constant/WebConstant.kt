package com.xiaoyv.bangumi.shared.data.constant

/**
 * [WebConstant]
 *
 * @author why
 * @since 2025/1/14
 */
object WebConstant {
    const val APP_ID = "bgm285565606da641d78"
    const val APP_SECRET = "2f6af7bc16f05f70537ec24076164d5c"
    const val APP_CALLBACK = "http://localhost/callback"
    const val URL_BASE_API = "https://api.bgm.tv/"
    const val URL_BASE_NEXT_API = "https://next.bgm.tv/"
    const val URL_BASE_APP_API = "https://api.xiaoyv.com.cn/"
    const val URL_BASE_API_DOUBAN = "https://frodo.douban.com/"
    const val URL_BASE_API_PIXIV = "https://app-api.pixiv.net/"
    const val URL_BASE_WEB = "https://bgm.tv/"
    const val URL_BGM_STATUS_WEB = "https://bgm-status.ry.mk/"
    const val URL_BGM_STATUS_API = "https://bgm-status.ry.mk/api/mini"
    const val URL_BGM_TURNSTILE = "https://next.bgm.tv/p1/turnstile?theme=auto&redirect_uri=bangumi://"
    const val URL_BGM_SIGN = "https://bgm.tv/signup"

    const val CLOUDFLARE_DNS_ENDPOINT_1 = "https://xwfpeb16ii.cloudflare-gateway.com/dns-query"
    const val CLOUDFLARE_DNS_ENDPOINT_2 = "https://2db48881951041908d8f6af02e689c77.xiaoyv.com.cn/dns-query"
}

fun subjectImage(id: Long, type: String = "large") = "https://api.bgm.tv/v0/subjects/$id/image?type=$type"
fun characterImage(id: Long, type: String = "large") = "https://api.bgm.tv/v0/characters/$id/image?type=$type"
fun personImage(id: Long, type: String = "large") = "https://api.bgm.tv/v0/persons/$id/image?type=$type"
fun userImage(username: String, type: String = "large") = "https://api.bgm.tv/v0/users/$username/avatar?type=$type"
