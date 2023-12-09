package com.xiaoyv.common.helper

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
    const val CACHE_HISTORY = "SearchHistory"

    fun saveSearchHistory(searchItem: SearchItem) {
        launchProcess {
            val key = EncryptUtils.encryptMD5ToString(searchItem.toString())
            searchItem.timestamp = System.currentTimeMillis()
            SPUtils.getInstance(CACHE_HISTORY).put(key, searchItem.toJson())
        }
    }

    fun readSearchHistory(): List<SearchItem> {
        return runCatching {
            SPUtils.getInstance(CACHE_HISTORY).all
                .map { it.value.toString().fromJson<SearchItem>() }
                .filterNotNull()
                .sortedByDescending { it.timestamp }
                .subListLimit(9)
        }.getOrNull().orEmpty()
    }
}