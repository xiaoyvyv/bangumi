package com.xiaoyv.bangumi.features.mono.detail.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailEvent
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailState
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoCollab
import com.xiaoyv.bangumi.shared.ui.component.image.InfoImage
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import com.xiaoyv.bangumi.shared.ui.theme.contentMarginHalf

/**
 * [MonoDetailCollabsScreen]
 *
 * 人物/角色的合作者页面，从 webInfo.collabs 获取数据
 *
 * @since 2025/5/18
 */
@Composable
fun MonoDetailCollabsScreen(
    state: MonoDetailState,
    onUiEvent: (MonoDetailEvent.UI) -> Unit,
    onActionEvent: (MonoDetailEvent.Action) -> Unit,
) {
    val collabs = state.mono.webInfo.collabs

    if (collabs.isEmpty()) {
        // 无合作数据时显示空状态
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentMargin),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "暂无合作信息",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(contentMargin),
        horizontalArrangement = Arrangement.spacedBy(contentMargin),
        verticalArrangement = Arrangement.spacedBy(contentMargin),
    ) {
        items(
            items = collabs,
            key = { it.id },
            contentType = { "Collab" }
        ) { collab ->
            CollabItem(
                collab = collab,
                onClick = {
                    onUiEvent(
                        MonoDetailEvent.UI.OnNavScreen(
                            Screen.MonoDetail(collab.id, MonoType.PERSON)
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun CollabItem(
    collab: ComposeMonoCollab,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickWithoutRipped(onClick = onClick)
            .width(100.dp)
            .padding(vertical = contentMarginHalf),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(contentMarginHalf),
    ) {
        InfoImage(
            modifier = Modifier
                .width(70.dp),
            model = collab.images.displayMediumImage,
            onClick = onClick,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = collab.name,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
        )

        if (collab.count.isNotBlank()) {
            Text(
                text = "${collab.count}次合作",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
