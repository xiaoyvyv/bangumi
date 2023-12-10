package com.xiaoyv.common.helper

import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.SPUtils
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.subListLimit

/**
 * Class: [CacheHelper]
 *
 * @author why
 * @since 12/10/23
 */
object CacheHelper {
    private const val CACHE_HISTORY = "SearchHistory"

    /**
     * 保存搜索历史
     */
    fun saveSearchHistory(searchItem: SearchItem) {
        if (searchItem.keyword.isBlank()) return
        launchProcess {
            val key = EncryptUtils.encryptMD5ToString(searchItem.keyword)
            searchItem.timestamp = System.currentTimeMillis()
            SPUtils.getInstance(CACHE_HISTORY).put(key, searchItem.toJson())
        }
    }

    /**
     * 读取搜索历史
     */
    fun readSearchHistory(): List<SearchItem> {
        return runCatching {
            SPUtils.getInstance(CACHE_HISTORY).all
                .map { it.value.toString().fromJson<SearchItem>() }
                .filterNotNull()
                .sortedByDescending { it.timestamp }
                .subListLimit(9)
        }.getOrNull().orEmpty()
    }

    fun saveTranslate(cacheKey: String, text: String) {
        CacheDiskUtils.getInstance().put(cacheKey, text)
    }

    fun readTranslate(cacheKey: String): String {
        return CacheDiskUtils.getInstance().getString(cacheKey).orEmpty()
    }
}