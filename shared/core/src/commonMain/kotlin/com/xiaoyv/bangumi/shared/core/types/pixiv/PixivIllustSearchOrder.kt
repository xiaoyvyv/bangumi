package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_search_order_female_popular
import com.xiaoyv.bangumi.core_resource.resources.pixiv_search_order_latest
import com.xiaoyv.bangumi.core_resource.resources.pixiv_search_order_male_popular
import com.xiaoyv.bangumi.core_resource.resources.pixiv_search_order_oldest
import com.xiaoyv.bangumi.core_resource.resources.pixiv_search_order_popular
import org.jetbrains.compose.resources.StringResource

/**
 * Pixiv 作品标签搜索支持的排序方式。
 */
@StringDef(
    PixivIllustSearchOrder.POPULAR,
    PixivIllustSearchOrder.LATEST,
    PixivIllustSearchOrder.OLDEST,
    PixivIllustSearchOrder.MALE_POPULAR,
    PixivIllustSearchOrder.FEMALE_POPULAR,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustSearchOrder {
    companion object {
        const val POPULAR = "popular_d"
        const val LATEST = "date_d"
        const val OLDEST = "date"
        const val MALE_POPULAR = "popular_male_d"
        const val FEMALE_POPULAR = "popular_female_d"

        fun label(@PixivIllustSearchOrder order: String): StringResource {
            return when (order) {
                POPULAR -> Res.string.pixiv_search_order_popular
                OLDEST -> Res.string.pixiv_search_order_oldest
                MALE_POPULAR -> Res.string.pixiv_search_order_male_popular
                FEMALE_POPULAR -> Res.string.pixiv_search_order_female_popular
                else -> Res.string.pixiv_search_order_latest
            }
        }
    }
}
