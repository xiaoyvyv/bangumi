package com.xiaoyv.common.kts

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Parcelable
import androidx.annotation.Keep
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xiaoyv.blueprint.kts.toJson


typealias GoogleAttr = com.google.android.material.R.attr
typealias GoogleStyle = com.google.android.material.R.style
typealias CommonDrawable = com.xiaoyv.common.R.drawable
typealias CommonId = com.xiaoyv.common.R.id
typealias CommonString = com.xiaoyv.common.R.string
typealias CommonColor = com.xiaoyv.common.R.color

val gson by lazy { Gson() }

fun openInBrowser(url: String) {
    runCatching {
        ActivityUtils.startActivity(Intent.parseUri(url, Intent.URI_ALLOW_UNSAFE))
    }
}

@Keep
inline fun <reified T> String.fromJson(): T? {
    runCatching {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(this, type)
    }.onFailure {
        it.printStackTrace()

        debugLog { "JsonError: $this" }
    }
    return null
}

val Int.tint: ColorStateList
    get() = ColorStateList.valueOf(this)

fun Any.toJsonMap(): Map<String, Any> {
    return toJson().fromJson<Map<String, Any>>().orEmpty()
}

@Keep
inline fun <reified T> Any.forceCast(): T {
    return this as T
}

@Keep
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Parcelable> parcelableCreator(): Parcelable.Creator<T> =
    T::class.java.getDeclaredField("CREATOR").get(null) as? Parcelable.Creator<T>
        ?: throw IllegalArgumentException("Could not access CREATOR field in class ${T::class.simpleName}")

fun randId(): String {
    return System.currentTimeMillis().toString()
}
