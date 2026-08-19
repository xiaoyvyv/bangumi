package com.xiaoyv.bangumi.shared.ui.component.image.html

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.library.HtmlDefaults
import com.xiaoyv.library.HtmlImageContent
import com.xiaoyv.library.HtmlImageData

val BgmHtmlImageContent: (HtmlImageData, Boolean) -> HtmlImageContent = { image, isInline ->
    val size = when {
        !isInline -> DpSize.Unspecified
        image.smileId?.startsWith("blake") == true -> DpSize(60.dp, 60.dp)
        image.smileId?.startsWith("musume") == true -> DpSize(60.dp, 60.dp)
        image.smileId?.startsWith("bmo") == true -> DpSize(24.dp, 24.dp)
        image.source.contains("/smiles/") -> DpSize(24.dp, 24.dp)
        else -> HtmlDefaults.imageSize(image, true)
    }
    HtmlImageContent(size) {
        if (isInline) {
            BgmInlineHtmlImage(image)
        } else {
            BgmBlockHtmlImage(image)
        }
    }
}

@Composable
fun BgmInlineHtmlImage(image: HtmlImageData) {
    val isSmile = !image.smileId.isNullOrBlank()
    val isSmallSmile = image.smileId?.startsWith("bgm") == true || image.smileId?.startsWith("bmo") == true
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp)
            .let { if (isSmallSmile) it.padding(bottom = 2.dp) else it },
        contentAlignment = Alignment.Center
    ) {
        StateImage(
            modifier = if (isSmile) Modifier.aspectRatio(1f) else Modifier.fillMaxSize(),
            model = image.source,
            contentDescription = image.alt,
            containerColor = if (isSmile) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer,
            filterQuality = if (isSmallSmile) FilterQuality.None else FilterQuality.High,
            blurLoading = !isSmile
        )
    }
}

@Composable
fun BgmBlockHtmlImage(image: HtmlImageData) {
    StateImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(2.dp),
        model = image.source,
    )
}