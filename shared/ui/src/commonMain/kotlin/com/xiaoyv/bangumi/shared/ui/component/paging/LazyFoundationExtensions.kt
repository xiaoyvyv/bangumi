package com.xiaoyv.bangumi.shared.ui.component.paging

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.paging.PagingConfig
import kotlin.jvm.JvmSuppressWildcards

/**
 * Returns a factory of stable and unique keys representing the item.
 *
 * Keys are generated with the key lambda that is passed in. If null is passed in, keys will
 * default to a placeholder key. If [PagingConfig.enablePlaceholders] is true,
 * LazyPagingItems may return null items. Null items will also automatically default to
 * a placeholder key.
 *
 * This factory can be applied to Lazy foundations such as [LazyGridScope.items] or Pagers.
 *
 * @param [key] a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. When you specify the key the scroll position will be maintained
 * based on the key, which means if you add/remove items before the current visible item the
 * item with the given key will be kept as the first visible one.
 */
fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)? = null,
): (index: Int) -> Any {
    return { index ->
        if (key == null) {
            getPagingPlaceholderKey(index)
        } else {
            val item = peek(index)
            if (item == null) getPagingPlaceholderKey(index) else key(item)
        }
    }
}

/**
 * Returns a factory for the content type of the item.
 *
 * ContentTypes are generated with the contentType lambda that is passed in. If null is passed in,
 * contentType of all items will default to `null`.
 * If [PagingConfig.enablePlaceholders] is true, LazyPagingItems may return null items. Null
 * items will automatically default to placeholder contentType.
 *
 * This factory can be applied to Lazy foundations such as [LazyGridScope.items] or Pagers.
 *
 * @param [contentType] a factory of the content types for the item. The item compositions of
 * the same type could be reused more efficiently. Note that null is a valid type and items of
 * such type will be considered compatible.
 */
fun <T : Any> LazyPagingItems<T>.itemContentType(
    contentType: ((item: @JvmSuppressWildcards T) -> Any?)? = null,
): (index: Int) -> Any? {
    return { index ->
        if (contentType == null) {
            null
        } else {
            val item = peek(index)
            if (item == null) PagingPlaceholderContentType else contentType(item)
        }
    }
}
