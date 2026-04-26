package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.serialization.KSerializer

/**
 * [DatabaseRepository]
 *
 * @author why
 * @since 2025/1/15
 */
interface DatabaseRepository {
    fun fetchCurrentUser(): ComposeUser
    fun sendSaveUser(composeUser: ComposeUser)

    /**
     * 取出
     */
    fun getString(key: String, default: String = ""): String
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun getLong(key: String, default: Long = 0): Long
    fun getShort(key: String, default: Short = 0): Short
    fun getInt(key: String, default: Int = 0): Int
    fun getFloat(key: String, default: Float = 0f): Float
    fun <T> get(key: String, default: T, deserializable: KSerializer<T>): T

    /**
     * 存入
     */
    fun put(key: String, value: String)
    fun put(key: String, value: Boolean)
    fun put(key: String, value: Long)
    fun put(key: String, value: Short)
    fun put(key: String, value: Int)
    fun put(key: String, value: Float)
    fun <T> put(key: String, data: T, serializable: KSerializer<T>)
}