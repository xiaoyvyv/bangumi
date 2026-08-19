@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.manager

import com.xiaoyv.bangumi.shared.core.utils.getBangumiSmileUrl
import com.xiaoyv.library.convert.BBCodeToHtml
import com.xiaoyv.library.convert.DefaultSmileTextReplacement


val bbCodeConverter = BBCodeToHtml(listOf(BgmDefaultSmileTextReplacement()))

fun String.bbcodeToHtml(): String {
    return bbCodeConverter.convert(this)
}

class BgmDefaultSmileTextReplacement : DefaultSmileTextReplacement() {
    override fun renderSmile(smileId: String, prefix: String): String {
        val url = getBangumiSmileUrl(smileId) ?: "https://lain.bgm.tv/img/smiles/tv_vs/bgm_232.png"
        return "<img src=\"$url\" class=\"smile smile-$prefix\" smileid=\"$smileId\">"
    }
}