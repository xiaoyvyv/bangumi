package com.xiaoyv.bangumi.shared.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xiaoyv.bangumi.shared.core.types.SearchType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.constant.SpKey
import com.xiaoyv.bangumi.shared.data.manager.app.SearchPreferences
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchPreferencesTest {
    @Test
    fun newInstallKeepsTheExistingAnimeSearchDefault() {
        val preferences = SearchPreferences(MemoryCache())
        assertEquals(SearchType.SUBJECT, preferences.searchType)
        assertEquals(SubjectType.ANIME, preferences.subjectType)
    }

    @Test
    fun reopeningSearchRestoresCategoryAndSubjectTypeIndependently() {
        val cache = MemoryCache()
        val firstSearch = SearchPreferences(cache)
        firstSearch.subjectType = SubjectType.BOOK
        firstSearch.searchType = SearchType.CHARACTER

        val reopenedSearch = SearchPreferences(cache)
        assertEquals(SearchType.CHARACTER, reopenedSearch.searchType)
        assertEquals(SubjectType.BOOK, reopenedSearch.subjectType)

        reopenedSearch.searchType = SearchType.SUBJECT
        assertEquals(SubjectType.BOOK, SearchPreferences(cache).subjectType)
    }

    @Test
    fun removedOrInvalidTypesFallBackWithoutOverwritingValidChoices() {
        val cache = MemoryCache()
        cache.values[stringPreferencesKey(SpKey.KEY_LAST_SEARCH_TYPE)] = "removed-category"
        cache.values[intPreferencesKey(SpKey.KEY_LAST_SEARCH_SUBJECT_TYPE)] = -1
        val preferences = SearchPreferences(cache)
        assertEquals(SearchType.SUBJECT, preferences.searchType)
        assertEquals(SubjectType.ANIME, preferences.subjectType)

        preferences.subjectType = SubjectType.GAME
        preferences.subjectType = -1
        assertEquals(SubjectType.GAME, SearchPreferences(cache).subjectType)
    }

    private class MemoryCache : CacheRepository {
        val values = mutableMapOf<Preferences.Key<*>, Any>()

        @Suppress("UNCHECKED_CAST")
        override fun <T : Any> readSync(key: Preferences.Key<T>): T? = values[key] as T?

        override fun <T : Any> readSync(key: Preferences.Key<T>, default: T): T = readSync(key) ?: default

        override suspend fun <T : Any> read(key: Preferences.Key<T>): T? = readSync(key)

        override suspend fun <T : Any> write(key: Preferences.Key<T>, value: T): Result<Unit> {
            values[key] = value
            return Result.success(Unit)
        }
    }
}
