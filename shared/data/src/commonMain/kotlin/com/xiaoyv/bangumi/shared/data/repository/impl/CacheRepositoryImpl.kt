@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * [CacheRepositoryImpl]
 *
 * @since 2025/5/11
 */
class CacheRepositoryImpl : CacheRepository {

    override fun <T : Any> readSync(key: Preferences.Key<T>) = runBlocking { read(key) }
    override fun <T : Any> readSync(key: Preferences.Key<T>, default: T): T = runBlocking { read(key) ?: default }

    override suspend fun <T : Any> read(key: Preferences.Key<T>): T? {
        return System.datastore.data.map { it[key] }.firstOrNull()
    }

    override suspend fun <T : Any> write(key: Preferences.Key<T>, value: T) {
        System.datastore.edit { it[key] = value }
    }
}