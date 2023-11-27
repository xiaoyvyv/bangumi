package com.xiaoyv.common.widget.emoji.edit

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.collection.LruCache
import androidx.core.content.ContextCompat

/**
 * Cache 里面默认只存放32个表情
 *
 * @author why
 */
class UiFaceCache private constructor(cacheSize: Int) {

    private val mCache: LruCache<Int, Drawable> = object : LruCache<Int, Drawable>(cacheSize) {
        override fun sizeOf(key: Int, value: Drawable): Int {
            return 1
        }

        override fun entryRemoved(evicted: Boolean, key: Int, oldValue: Drawable, newValue: Drawable?) {}
    }

    fun getDrawable(context: Context?, resourceId: Int): Drawable? {
        var drawable = mCache[resourceId]
        if (drawable == null) {
            drawable = ContextCompat.getDrawable(context!!, resourceId)
            if (drawable != null) {
                mCache.put(resourceId, drawable)
            }
        }
        return drawable
    }

    companion object {
        private const val EMOJI_CACHE_SIZE = 32

        @Volatile
        @JvmStatic
        private var instance: UiFaceCache? = null

        @JvmStatic
        fun getInstance(): UiFaceCache {
            if (instance == null) {
                synchronized(UiFaceCache::class.java) {
                    if (instance == null) {
                        instance = UiFaceCache(EMOJI_CACHE_SIZE)
                    }
                }
            }
            return instance!!
        }
    }
}