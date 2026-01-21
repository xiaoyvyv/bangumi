package com.xiaoyv.bangumi.features.preivew.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_file_size
import com.xiaoyv.bangumi.core_resource.resources.global_resolution
import com.xiaoyv.bangumi.features.preivew.album.business.PreviewAlbumEvent
import com.xiaoyv.bangumi.features.preivew.album.business.PreviewAlbumState
import com.xiaoyv.bangumi.features.preivew.album.business.PreviewAlbumViewModel
import com.xiaoyv.bangumi.features.preivew.album.business.rememberPreviewAlbumViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.core.utils.formatFileSize
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalStaggeredGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfBlack
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewAlbumRoute(
    param: ListAlbumParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: PreviewAlbumViewModel = rememberPreviewAlbumViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.album.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    PreviewAlbumScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PreviewAlbumEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PreviewAlbumScreen(
    baseState: BaseState<PreviewAlbumState>,
    pagingItems: LazyPagingItems<ComposeGallery>,
    onUiEvent: (PreviewAlbumEvent.UI) -> Unit,
    onActionEvent: (PreviewAlbumEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(PreviewAlbumEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        PreviewAlbumScreenContent(state, pagingItems, onUiEvent, onActionEvent)
    }
}


@Composable
private fun PreviewAlbumScreenContent(
    state: PreviewAlbumState,
    pagingItems: LazyPagingItems<ComposeGallery>,
    onUiEvent: (PreviewAlbumEvent.UI) -> Unit,
    onActionEvent: (PreviewAlbumEvent.Action) -> Unit,
) {
    StateLazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(180.dp),
        pagingItems = pagingItems,
        key = { item, _ -> item.id },
        contentType = { "AlbumItem" }
    ) { item, index ->
        MonoDetailPictureItem(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(item.aspect),
            item = item,
            onClick = {
                if (state.type == ListAlbumType.PIVIX) {
                    onUiEvent(PreviewAlbumEvent.UI.OnNavScreen(Screen.Gallery(item.id, item.type)))
                } else {
                    val items = pagingItems.itemSnapshotList.mapNotNull { it?.image }
                    val current = items.indexOf(item.image).coerceAtLeast(0)
                    if (items.isNotEmpty()) {
                        onUiEvent(PreviewAlbumEvent.UI.OnNavScreen(Screen.PreviewMain(current, items)))
                    }
                }
            }
        )
    }
}

@Composable
private fun MonoDetailPictureItem(
    modifier: Modifier,
    item: ComposeGallery,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.clickable(onClick = onClick).then(modifier)) {
        StateImage(
            modifier = Modifier
                .matchParentSize()
                .background(item.uiColor),
            model = item.image,
            alignment = Alignment.TopCenter,
        )

        if (item.count > 1) Text(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(LayoutPaddingHalf)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.Black.copy(0.5f))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            color = Color.White,
            text = item.count.toString(),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(BrushVerticalTransparentToHalfBlack)
                .padding(LayoutPaddingHalf),
            style = MaterialTheme.typography.bodySmall.copy(
                shadow = Shadow(
                    color = if (item.uiColor != Color.Unspecified) item.uiColor else Color.White,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            color = Color.White,
            text = buildString {
                if (item.width > 0 && item.height > 0) {
                    append(stringResource(Res.string.global_resolution, item.width, item.height))
                }
                if (item.info.isNotEmpty()) {
                    append(item.info)
                }
                if (item.size > 0) {
                    appendLine()
                    append(stringResource(Res.string.global_file_size, item.size.formatFileSize()))
                }
            }
        )
    }
}
