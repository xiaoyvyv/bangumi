package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.IntDef

/**
 * Pixiv Ajax 搜索端点支持的 AI 作品显示选项。
 */
@IntDef(
    PixivIllustSearchAiType.HIDE,
    PixivIllustSearchAiType.SHOW,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustSearchAiType {
    companion object {
        const val HIDE = 0
        const val SHOW = 1
    }
}
