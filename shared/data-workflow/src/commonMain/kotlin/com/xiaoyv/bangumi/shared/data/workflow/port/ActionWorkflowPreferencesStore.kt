package com.xiaoyv.bangumi.shared.data.workflow.port

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore.Companion.KEY_PREFIX
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore.Companion.storageKey
import com.xiaoyv.bangumi.shared.libnative.System
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement

/**
 * 工作流私有 Preferences 存储端口。
 *
 * 实现必须通过 [storageKey] 生成真实键，确保工作流数据不会覆盖应用其他设置。
 */
interface ActionWorkflowPreferencesStore {
    /**
     * 读取工作流私有键对应的 JSON 值。
     *
     * @param key 不包含 [KEY_PREFIX] 的业务键。
     */
    suspend fun get(key: String): JsonElement?

    /**
     * 写入工作流私有键对应的 JSON 值。
     *
     * @param key 不包含 [KEY_PREFIX] 的业务键。
     * @param value 要持久化的 JSON 值。
     */
    suspend fun set(key: String, value: JsonElement)

    /**
     * 删除工作流私有键。
     *
     * @param key 不包含 [KEY_PREFIX] 的业务键。
     */
    suspend fun delete(key: String)

    /**
     * 判断工作流私有键是否存在；已存储 JSON null 也视为存在。
     *
     * @param key 不包含 [KEY_PREFIX] 的业务键。
     */
    suspend fun has(key: String): Boolean

    /**
     * 清空当前工作流命名空间下的全部键，不影响应用其他 Preferences 数据。
     */
    suspend fun clear()

    companion object {
        /**
         * 工作流所有 Preferences 键必须使用的前缀。
         */
        const val KEY_PREFIX = "workflow_"

        /**
         * 生成隔离后的真实 Preferences 键。
         */
        fun storageKey(key: String): String {
            require(key.isNotBlank()) { "工作流存储 key 不能为空" }
            require(!key.startsWith(KEY_PREFIX)) { "工作流存储 key 不应重复包含 $KEY_PREFIX 前缀" }
            return KEY_PREFIX + key
        }
    }
}

/**
 * 基于 [System.datastore] 的默认工作流私有存储实现。
 */
class DefaultActionWorkflowPreferencesStore : ActionWorkflowPreferencesStore {
    override suspend fun get(key: String): JsonElement? {
        val stored = System.datastore.data.first()[stringPreferencesKey(storageKey(key))]
            ?: return null
        return defaultJson.parseToJsonElement(stored)
    }

    override suspend fun set(key: String, value: JsonElement) {
        System.datastore.edit { preferences ->
            preferences[stringPreferencesKey(storageKey(key))] = value.toString()
        }
    }

    override suspend fun delete(key: String) {
        System.datastore.edit { preferences ->
            preferences.remove(stringPreferencesKey(storageKey(key)))
        }
    }

    override suspend fun has(key: String): Boolean {
        return System.datastore.data.first().contains(stringPreferencesKey(storageKey(key)))
    }

    override suspend fun clear() {
        System.datastore.edit { preferences ->
            preferences.asMap().keys
                .filter { it.name.startsWith(KEY_PREFIX) }
                .forEach { key -> preferences.remove(stringPreferencesKey(key.name)) }
        }
    }
}
