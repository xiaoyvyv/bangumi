package com.xiaoyv.common.kts

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xiaoyv.blueprint.kts.toJson


typealias GoogleAttr = com.google.android.material.R.attr
typealias CommonDrawable = com.xiaoyv.common.R.drawable
typealias CommonString = com.xiaoyv.common.R.string

val gson by lazy { Gson() }

fun openInBrowser(url: String) {
    runCatching {
        ActivityUtils.startActivity(Intent.parseUri(url, Intent.URI_ALLOW_UNSAFE))
    }
}

inline fun <reified T> String.fromJson(): T? {
    runCatching {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(this, type)
    }.onFailure {
        LogUtils.e("JsonError: $this", it)
    }
    return null
}

fun Any.toJsonMap(): Map<String, Any> {
    return toJson().fromJson<Map<String, Any>>().orEmpty()
}