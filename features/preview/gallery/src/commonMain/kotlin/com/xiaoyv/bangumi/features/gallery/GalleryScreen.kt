package com.xiaoyv.bangumi.features.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_artwork
import com.xiaoyv.bangumi.core_resource.resources.global_file_size
import com.xiaoyv.bangumi.core_resource.resources.global_resolution
import com.xiaoyv.bangumi.features.gallery.business.GalleryEvent
import com.xiaoyv.bangumi.features.gallery.business.GalleryState
import com.xiaoyv.bangumi.features.gallery.business.GalleryViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.formatFileSize
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfBlack
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GalleryRoute(
    viewModel: GalleryViewModel = koinViewModel<GalleryViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    GalleryScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is GalleryEvent.UI.OnNavUp -> onNavUp()
                is GalleryEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun GalleryScreen(
    baseState: BaseState<GalleryState>,
    onUiEvent: (GalleryEvent.UI) -> Unit,
    onActionEvent: (GalleryEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.payload.let {
                    if (it == null) stringResource(Res.string.global_artwork)
                    else stringResource(Res.string.global_artwork) + "：${it.id}"
                },
                onNavigationClick = { onUiEvent(GalleryEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(GalleryEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            GalleryScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun GalleryScreenContent(
    state: GalleryState,
    onUiEvent: (GalleryEvent.UI) -> Unit,
    onActionEvent: (GalleryEvent.Action) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Adaptive(350.dp)
    ) {
        itemsIndexed(state.images) { index, item ->
            GalleryPictureItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(item.aspect),
                item = item,
                onClick = {
                    onUiEvent(GalleryEvent.UI.OnNavScreen(Screen.PreviewMain(index, state.images.map { it.image })))
                }
            )
        }
    }
}


@Composable
private fun GalleryPictureItem(
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
            transparent = true
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
                append(stringResource(Res.string.global_resolution, item.width, item.height))
                if (item.size > 0) {
                    appendLine()
                    append(stringResource(Res.string.global_file_size, item.size.formatFileSize()))
                }
            }
        )
    }
}

