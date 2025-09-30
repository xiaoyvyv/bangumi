package com.xiaoyv.bangumi.features.index.page.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.BottomSheetDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.index.IndexDialogItem
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun IndexDialog(
    state: BottomSheetDialogState,
    target: IndexTarget,
) {
    BottomSheetDialog(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 120.dp),
        state = state,
        contentWindowInsets = { WindowInsets() },
    ) {
        val viewModel = koinIndexDialogViewModel(target = target)
        val baseState by viewModel.collectAsState()

        viewModel.collectBaseSideEffect {
            when (it) {
                IndexDialogSideEffect.OnSaveSuccess -> state.dismiss()
            }
        }

        LaunchedEffect(target) {
            viewModel.onEvent(IndexDialogEvent.Action.OnRefreshCollection)
        }

        StateLayout(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            onRefresh = { loading -> viewModel.onEvent(IndexDialogEvent.Action.OnRefresh(loading)) },
            baseState = baseState,
        ) { state ->
            IndexDialogContent(
                state = state,
                target = target,
                onActionEvent = { viewModel.onEvent(it) }
            )
        }
    }
}

@Composable
fun IndexDialogContent(
    state: IndexDialogState,
    target: IndexTarget,
    onActionEvent: (IndexDialogEvent.Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPadding),
            text = target.displayName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPadding),
            text = "收集至我的目录",
            style = MaterialTheme.typography.bodyLarge,
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.indexList) {
                IndexDialogItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = it,
                    onClick = {
                        onActionEvent(IndexDialogEvent.Action.OnSaveToCollection(it.id))
                    }
                )
            }
        }
    }
}