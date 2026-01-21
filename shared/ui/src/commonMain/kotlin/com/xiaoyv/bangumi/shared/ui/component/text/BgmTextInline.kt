package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.xiaoyv.bangumi.shared.core.utils.bgmEmojis
import com.xiaoyv.bangumi.shared.resource.toComposeUri
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.compose.resources.DrawableResource

const val InlineTextContentIdStar = "star"

val StarColor = Color(0xFFFFAA00)

val InlineTextContentMap by lazy {
    val inlineMap = mutableMapOf<String, InlineTextContent>()
    bgmEmojis.forEach {
        inlineMap.put(it.key, createIconInlineContent(it.value.image, 20.dp))
    }
    inlineMap.put(InlineTextContentIdStar, createIconInlineContent(BgmIcons.Star, 20.dp, StarColor))
    inlineMap.toImmutableMap()
}

private fun createIconInlineContent(
    icon: DrawableResource,
    size: Dp,
): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(size.value.sp, size.value.sp, PlaceholderVerticalAlign.Center),
        children = {
            val context = LocalPlatformContext.current
            AsyncImage(
                modifier = Modifier
                    .padding(2.dp)
                    .size(size),
                model = remember(icon) {
                    ImageRequest.Builder(context)
                        .data(icon.toComposeUri())
                        .build()
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
                filterQuality = FilterQuality.None
            )
        }
    )
}

private fun createIconInlineContent(
    icon: ImageVector,
    size: Dp,
    tint: Color = Color.Unspecified,
): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(size.value.sp, size.value.sp, PlaceholderVerticalAlign.Center),
        children = {
            Icon(
                modifier = Modifier
                    .padding(2.dp)
                    .size(size),
                imageVector = icon,
                tint = tint,
                contentDescription = null
            )
        }
    )
}

fun createImageInlineContent(
    width: TextUnit,
    height: TextUnit,
): InlineTextContent {
    return InlineTextContent(
        placeholder = Placeholder(width, height, PlaceholderVerticalAlign.Center),
        children = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                if (it.isNotEmpty()) {
                    BlurImage(
                        modifier = Modifier.fillMaxSize(),
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        filterQuality = FilterQuality.None,
                        androidRadius = 15,
                        androidSampling = 10f
                    )
                }
                StateImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    model = it,
                )
            }
        }
    )
}