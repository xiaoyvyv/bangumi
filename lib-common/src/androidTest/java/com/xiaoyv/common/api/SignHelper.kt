package com.xiaoyv.common.api

import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import com.blankj.utilcode.util.EncryptUtils
import okhttp3.HttpUrl.Companion.toHttpUrl


/**
 * Class: [SignHelper]
 *
 * @author why
 * @since 12/11/23
 */
object SignHelper {

    fun pair(requestUrl: String, method: String, bearToken: String): Pair<String, String> {
        val key = "bf7dddc7c9cfe6f7"
        val encodedPath = requestUrl.toHttpUrl().encodedPath
        val decode = Uri.decode(encodedPath).trimEnd('/')

        val builder = StringBuilder(method)
        builder.append("&")
        builder.append(Uri.encode(decode))

        if (!TextUtils.isEmpty(bearToken)) {
            builder.append("&")
            builder.append(bearToken)
        }

        val currentTimeMillis = System.currentTimeMillis() / 1000
        builder.append("&")
        builder.append(currentTimeMillis)

        val paramString = builder.toString()
        val bytes = EncryptUtils.encryptHmacSHA1(paramString.toByteArray(), key.toByteArray())
        val sign = Base64.encodeToString(bytes, 2)
        return Pair(sign, currentTimeMillis.toString())
    }
}