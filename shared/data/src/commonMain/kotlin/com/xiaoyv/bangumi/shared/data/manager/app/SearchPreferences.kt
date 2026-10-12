package com.xiaoyv.bangumi.shared.data.manager.app

import com.xiaoyv.bangumi.shared.core.types.SearchType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.constant.SpKey
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.int
import com.xiaoyv.bangumi.shared.data.repository.string

/**
 * 保存上次使用的搜索分类及条目类型，忽略旧配置中的无效值。
 *
 * @param cacheRepository 本地偏好存储。
 */
class SearchPreferences(cacheRepository: CacheRepository) {
    private var storedSearchType by cacheRepository.string(SpKey.KEY_LAST_SEARCH_TYPE, SearchType.SUBJECT)
    private var storedSubjectType by cacheRepository.int(SpKey.KEY_LAST_SEARCH_SUBJECT_TYPE, SubjectType.ANIME)

    @get:SearchType
    @setparam:SearchType
    var searchType: String
        get() = storedSearchType.takeIf { it in supportedSearchTypes } ?: SearchType.SUBJECT
        set(value) {
            if (value in supportedSearchTypes) storedSearchType = value
        }

    @get:SubjectType
    @setparam:SubjectType
    var subjectType: Int
        get() = storedSubjectType.takeIf { it in supportedSubjectTypes } ?: SubjectType.ANIME
        set(value) {
            if (value in supportedSubjectTypes) storedSubjectType = value
        }

    private companion object {
        val supportedSearchTypes = setOf(
            SearchType.SUBJECT, SearchType.CHARACTER, SearchType.PERSON,
            SearchType.TOPIC, SearchType.INDEX, SearchType.TAG,
        )
        val supportedSubjectTypes = setOf(
            SubjectType.ANIME, SubjectType.BOOK, SubjectType.MUSIC, SubjectType.GAME, SubjectType.REAL,
        )
    }
}
