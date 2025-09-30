package com.xiaoyv.bangumi.shared.ui.view.index

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.usecase.ImageRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.image.BlurImage
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import org.koin.compose.currentKoinScope
import kotlin.math.sqrt

typealias ImageData = BaseState<SerializeList<ComposeImages>>

@Composable
fun IndexFocusCard(
    item: ComposeIndex,
    modifier: Modifier = Modifier,
) {
    val koinScope = currentKoinScope()
    val images by produceState(BaseState.Loading(), item.id, koinScope) {
        value = BaseState.Success(koinScope.get<ImageRepoUseCase>().fetchIndexBlogCover(item.id))
    }
    val payload = images.payload

    Box(
        modifier = modifier
            .border(4.dp, MaterialTheme.colorScheme.onSurface)
            .shadow(1.dp)
    ) {
        if (payload.isNullOrEmpty()) {
            BlurImage(
                modifier = Modifier.matchParentSize(),
                model = item.creator.displayAvatar,
                contentDescription = null
            )
        } else {
            IndexFocusCard(
                modifier = modifier,
                images = payload
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                .padding(LayoutPaddingHalf),
            text = item.title,
            maxLines = 2,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun IndexFocusCard(
    images: SerializeList<ComposeImages>,
    modifier: Modifier = Modifier,
) {
    // 只处理 1..16 的情况，超出丢弃
    val count = images.size.coerceIn(1, 16)
    val layout = remember(count) { generateGridLayout(count) }

    BoxWithConstraints(modifier = modifier) {
        // 保证容器为正方形：取宽高最小值
        val containerSize: Dp = min(maxWidth, maxHeight)
        val cols = layout.cols
        val cellSize = containerSize / cols.toFloat()

        Box(modifier = Modifier.size(containerSize)) {
            // 按 layout.cells 顺序将图片映射到格子上，多余图片将被丢弃
            layout.cells.forEachIndexed { index, cell ->
                val img = images.getOrNull(index) ?: return@forEachIndexed
                val context = LocalPlatformContext.current
                val density = LocalDensity.current
                val width = cellSize * cell.colSpan.toFloat()
                val height = cellSize * cell.rowSpan.toFloat()
                val size = with(density) { Size(width.roundToPx(), height.roundToPx()) }

                AsyncImage(
                    contentDescription = null,
                    model = remember(img) {
                        ImageRequest.Builder(context)
                            .data(img.displayBlurUrl)
                            .size(size)
                            .build()
                    },
                    modifier = Modifier
                        .size(width, height)
                        .offset(x = cellSize * cell.col.toFloat(), y = cellSize * cell.row.toFloat()),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

/** 单元格定义（相对坐标：row/col/rowSpan/colSpan，均为整数） */
private data class GridCell(
    val row: Int,
    val col: Int,
    val rowSpan: Int = 1,
    val colSpan: Int = 1,
)

/** 返回布局信息：cols 表示每行格子数（渲染时使用 cols 计算单元格大小） */
private data class Layout(val cols: Int, val cells: List<GridCell>)

/** 入口：根据图片数量返回对应 Layout */
private fun generateGridLayout(count: Int): Layout {
    return when (count) {
        // 均匀分配：1x1, 2x2, 3x3, 4x4
        1 -> generateUniformLayout(1)
        4 -> generateUniformLayout(2)
        9 -> generateUniformLayout(3)
        16 -> generateUniformLayout(4)

        // 2、3 当 1 处理
        2, 3 -> generateUniformLayout(1)

        // 5、6、7 当 4 处理（多余丢弃）
        5, 6, 7 -> generateUniformLayout(2)

        // 8：第一张 3x3（占 9 格），其余 7 张各 1 格（使用 4 列基准）
        8 -> {
            val cells = mutableListOf<GridCell>()
            cells += GridCell(0, 0, 3, 3) // 大图
            val smalls = listOf(
                0 to 3, 1 to 3, 2 to 3,
                3 to 0, 3 to 1, 3 to 2, 3 to 3
            )
            cells += smalls.map { (r, c) -> GridCell(r, c) }
            Layout(cols = 4, cells = cells)
        }

        // 10~12 当 9 处理（多余丢弃）
        10, 11, 12 -> generateUniformLayout(3)

        // 13：第一张 2x2，其余单格（按行优先）
        13 -> {
            val cells = mutableListOf<GridCell>()
            cells += GridCell(0, 0, 2, 2) // 大图占 2x2
            // 其余按 4x4 的其他格子顺序填充（row-major）
            for (r in 0 until 4) {
                for (c in 0 until 4) {
                    if (r < 2 && c < 2) continue // 跳过大图已占的 2x2
                    if (cells.size >= 13) break
                    cells += GridCell(r, c)
                }
                if (cells.size >= 13) break
            }
            Layout(cols = 4, cells = cells)
        }

        // 14、15 当 13 处理（多余丢弃）
        14, 15 -> generateGridLayout(13)

        // 兜底：如果未来有其他情况，按最接近的正方形均匀分配（这里选 sqrt 向上）
        else -> {
            val grid = kotlin.math.ceil(sqrt(count.toDouble())).toInt()
            generateUniformLayout(grid)
        }
    }
}

/** 生成均匀的 n x n 网格（返回 cols = n，cells 为 n*n 的 row-major 列表） */
private fun generateUniformLayout(n: Int): Layout {
    val cells = mutableListOf<GridCell>()
    val total = n * n
    for (i in 0 until total) {
        val r = i / n
        val c = i % n
        cells += GridCell(r, c, 1, 1)
    }
    return Layout(cols = n, cells = cells)
}
