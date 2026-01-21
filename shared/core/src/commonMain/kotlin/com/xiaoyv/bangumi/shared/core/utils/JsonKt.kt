@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.core.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

val defaultJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    coerceInputValues = true
    encodeDefaults = true
}

val defaultProtoBuf = ProtoBuf {
}


inline fun <reified T : Any> String?.fromJson(): T? {
    val data = this ?: return null
    return runCatching { defaultJson.decodeFromString<T>(data) }.getOrNull()
}

inline fun <reified T : Any> T?.toJson(): String {
    val default = if (this is Collection<*>?) "[]" else "{}"
    return defaultJson.encodeToString(this ?: return default)
}

