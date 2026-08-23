package com.xiaoyv.bangumi.features.pixiv.main.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.features.pixiv.illust.page.IllustPageRoute
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.shared.core.types.list.ListIllustType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingMode
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.IllustRankBody
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PixivMainPageScreen(
    @PixivRankingContentType content: String,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    modifier: Modifier = Modifier,
) {
    val supportedModes = remember(content) {
        PixivRankingContentType.getSupportedModes(content)
    }

    val modeTabs = remember(supportedModes) {
        supportedModes.map { mode ->
            ComposeTextTab(mode, PixivRankingMode.label(mode))
        }.toPersistentList()
    }

    BgmChipHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = modeTabs,
    ) { page ->
        val mode = modeTabs[page].type
        IllustPageRoute(
            param = ListIllustParam(
                type = ListIllustType.RANK,
                ui = PageUI(gridLayout = true),
                rank = IllustRankBody(
                    content = content,
                    mode = mode
                )
            ),
            onNavScreen = { onUiEvent(PixivMainEvent.UI.OnNavScreen(it)) }
        )
    }
}

@Composable
@Preview
private fun PreviewPixivMainPageScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivMainPageScreen(
            content = PixivRankingContentType.ILLUST,
            onUiEvent = {}
        )
    }
}
