package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * [DatabaseRepositoryImpl]
 *
 * @author why
 * @since 2025/1/15
 */
class DatabaseRepositoryImpl(val json: Json) : DatabaseRepository {
    private val database get() = System.database

    override fun fetchCurrentUser(): ComposeUser {
        return ComposeUser.from(
            runCatching {
                database.appUserInfoQueries
                    .selectCurrentUser()
                    .executeAsOneOrNull()
            }.getOrNull()
        )
    }

    override fun sendSaveUser(composeUser: ComposeUser) {
        database.appUserInfoQueries.transaction {
            when {
                // 清空当前用户
                composeUser == ComposeUser.Empty -> database.appUserInfoQueries.deleteCurrentUser()
                // 存在则更新
                database.appUserInfoQueries.checkIdExists(composeUser.id).executeAsOne() -> database.appUserInfoQueries.updateUser(
                    id = composeUser.id,
                    avatar = composeUser.displayAvatar,
                    username = composeUser.username,
                    nickname = composeUser.nickname,
                    sign = composeUser.sign,
                    roomPic = composeUser.roomPic,
                    summary = composeUser.bio,
                    formHash = composeUser.formHash,
                    updateAt = composeUser.updateAt,
                    online = composeUser.online,
                    userGroup = composeUser.group
                )
                // 不存在则插入
                else -> database.appUserInfoQueries.insertUser(
                    id = composeUser.id,
                    avatar = composeUser.displayAvatar,
                    username = composeUser.username,
                    nickname = composeUser.nickname,
                    sign = composeUser.sign,
                    roomPic = composeUser.roomPic,
                    summary = composeUser.bio,
                    formHash = composeUser.formHash,
                    updateAt = composeUser.updateAt,
                    online = composeUser.online,
                    userGroup = composeUser.group
                )
            }
        }
    }

    override fun getString(key: String, default: String): String {
        val serializable = database.appSerializableQueries.selectByKey(key).executeAsOneOrNull()
        if (serializable == null) return default
        return serializable.value_
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return getString(key, default.toString()).toBooleanStrictOrNull() ?: default
    }

    override fun getLong(key: String, default: Long): Long {
        return getString(key, default.toString()).toLongOrNull() ?: default
    }

    override fun getShort(key: String, default: Short): Short {
        return getString(key, default.toString()).toShortOrNull() ?: default
    }

    override fun getInt(key: String, default: Int): Int {
        return getString(key, default.toString()).toIntOrNull() ?: default
    }

    override fun getFloat(key: String, default: Float): Float {
        return getString(key, default.toString()).toFloatOrNull() ?: default
    }

    override fun <T> put(key: String, data: T, serializable: KSerializer<T>) {
        put(key, json.encodeToString(serializable, data))
    }

    override fun put(key: String, value: String) {
        database.appSerializableQueries.insertOrReplace(
            key = key,
            value_ = value,
            createdAt = System.currentTimeMillis()
        )
    }

    override fun put(key: String, value: Boolean) {
        put(key, value.toString())
    }

    override fun put(key: String, value: Long) {
        put(key, value.toString())
    }

    override fun put(key: String, value: Short) {
        put(key, value.toString())
    }

    override fun put(key: String, value: Int) {
        put(key, value.toString())
    }

    override fun put(key: String, value: Float) {
        put(key, value.toString())
    }

    override fun <T> get(key: String, default: T, deserializable: KSerializer<T>): T {
        val value = getString(key, "")
        if (value.isBlank()) return default
        return try {
            json.decodeFromString(deserializable, value)
        } catch (_: Exception) {
            return default
        }
    }
}