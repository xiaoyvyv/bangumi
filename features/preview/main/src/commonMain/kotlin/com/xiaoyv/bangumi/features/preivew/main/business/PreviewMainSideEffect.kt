package com.xiaoyv.bangumi.features.preivew.main.business

import io.github.vinceglb.filekit.PlatformFile

/**
 * [PreviewMainSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PreviewMainSideEffect {
    data class OnSaveMedia(val file: PlatformFile) : PreviewMainSideEffect()
    data class OnShareMedia(val file: PlatformFile) : PreviewMainSideEffect()
    data class OnSetWallpaper(val file: PlatformFile) : PreviewMainSideEffect()
}