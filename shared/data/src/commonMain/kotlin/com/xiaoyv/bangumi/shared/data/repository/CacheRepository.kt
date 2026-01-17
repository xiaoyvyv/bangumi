@file:Suppress("UNCHECKED_CAST")
@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.measureBlockTimeMillis
import com.xiaoyv.bangumi.shared.core.utils.toJson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * [CacheRepository]
 *
 * @since 2025/5/11
 */
interface CacheRepository {
    fun <T : Any> readSync(key: Preferences.Key<T>): T?
    fun <T : Any> readSync(key: Preferences.Key<T>, default: T): T
    suspend fun <T : Any> read(key: Preferences.Key<T>): T?
    suspend fun <T : Any> write(key: Preferences.Key<T>, value: T)
}

inline fun <reified T : Any> BaseViewModel<T, *, *>.writeViewModelCache(
    cacheRepository: CacheRepository,
    cacheKey: Preferences.Key<String>,
    crossinline saveCondition: (T) -> Boolean = { true },
) {
    val baseState = container.stateFlow.value
    if (baseState is BaseState.Success) {
        viewModelScope.launch {
            if (saveCondition(baseState.data)) {
//                cacheRepository.write(cacheKey, defaultProtoBuf.encodeToHexString(baseState.data))
                cacheRepository.write(cacheKey, baseState.data.toJson())
            }
        }
    }
}

inline fun <reified T : Any> BaseViewModel<T, *, *>.readViewModelCache(
    cacheRepository: CacheRepository,
    cacheKey: Preferences.Key<String>,
    loadWhenEmpty: Boolean = false,
    enable: Boolean = true,
    transform: (T) -> T = { it },
): BaseState<T> {
    val text = if (enable) measureBlockTimeMillis { cacheRepository.readSync(cacheKey) } else null
    val cacheState = if (enable) measureBlockTimeMillis { text.fromJson<T>()?.let { transform(it) } } else null
//    val cacheState =
//        measureBlockTimeMillis {
//            runCatching { defaultProtoBuf.decodeFromHexString<T>(text.orEmpty()) }.onFailure { debugLog { it } }.getOrNull()
//                ?.let { transform(it) }
//        }
    debugLog { "$cacheState ,loadWhenEmpty=$loadWhenEmpty" }
    if (cacheState != null) return BaseState.Success(cacheState)
    return if (loadWhenEmpty) BaseState.Loading() else BaseState.Success(data = initSate(true))
}


fun CacheRepository.string(key: String, default: String = ""): ReadWriteProperty<Any, String> {
    return PreferenceDelegate(this, key, default)
}

fun CacheRepository.int(key: String, default: Int = 0): ReadWriteProperty<Any, Int> {
    return PreferenceDelegate(this, key, default)
}

fun CacheRepository.long(key: String, default: Long = 0): ReadWriteProperty<Any, Long> {
    return PreferenceDelegate(this, key, default)
}

fun CacheRepository.float(key: String, default: Float = 0f): ReadWriteProperty<Any, Float> {
    return PreferenceDelegate(this, key, default)
}

fun CacheRepository.boolean(key: String, default: Boolean = false): ReadWriteProperty<Any, Boolean> {
    return PreferenceDelegate(this, key, default)
}

fun CacheRepository.stringSet(
    key: String,
    default: Set<String> = emptySet(),
): ReadWriteProperty<Any, Set<String>> {
    return PreferenceDelegate(this, key, default)
}

inline fun <reified T : Any> CacheRepository.serializable(
    key: String,
    default: T,
): ReadWriteProperty<Any, T> {
    val serializer = defaultJson.serializersModule.serializer<T>()
    return PreferenceDelegate(this, key, default, serializer = serializer, json = defaultJson)
}

class PreferenceDelegate<T : Any>(
    private val prefs: CacheRepository,
    private val key: String,
    private val default: T,
    private val serializer: KSerializer<T>? = null,
    private val json: Json = defaultJson,
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (default) {
            is String -> prefs.readSync(stringPreferencesKey(key), default) as T
            is Int -> prefs.readSync(intPreferencesKey(key), default) as T
            is Long -> prefs.readSync(longPreferencesKey(key), default) as T
            is Float -> prefs.readSync(floatPreferencesKey(key), default) as T
            is Boolean -> prefs.readSync(booleanPreferencesKey(key), default) as T
            is Set<*> -> prefs.readSync(stringSetPreferencesKey(key), default as Set<String>) as T
            else -> {
                val string = prefs.readSync(stringPreferencesKey(key), "")
                if (string.isNotBlank()) {
                    try {
                        json.decodeFromString(requireNotNull(serializer), string)
                    } catch (_: Exception) {
                        default
                    }
                } else {
                    default
                }
            }
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        runBlocking {
            when (value) {
                is String -> prefs.write(stringPreferencesKey(key), value)
                is Int -> prefs.write(intPreferencesKey(key), value)
                is Long -> prefs.write(longPreferencesKey(key), value)
                is Float -> prefs.write(floatPreferencesKey(key), value)
                is Boolean -> prefs.write(booleanPreferencesKey(key), value)
                is Set<*> -> prefs.write(stringSetPreferencesKey(key), value as Set<String>)
                else -> {
                    val jsonString = json.encodeToString(requireNotNull(serializer), value)
                    prefs.write(stringPreferencesKey(key), jsonString)
                }
            }
        }
    }
}