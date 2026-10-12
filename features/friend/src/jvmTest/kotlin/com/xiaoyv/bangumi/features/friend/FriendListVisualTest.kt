@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.features.friend

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.friend.business.FriendItem
import com.xiaoyv.bangumi.features.friend.business.FriendState
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.ui.theme.BgmAppTheme
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingContentBottomPadding
import java.io.File
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.skia.EncodedImageFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 直接渲染按字母分组的好友列表，确保最后一人可以滚动到液态底栏上方。
 */
class FriendListVisualTest {
    @Test
    fun groupedFriendListKeepsItsLastPersonAboveFloatingBar() {
        for (style in listOf(SettingUiStyle.MATERIAL3, SettingUiStyle.MIUIX)) {
            for (dark in listOf(false, true)) {
                val listState = LazyListState(firstVisibleItemIndex = 20)
                val friends = (listOf(FriendItem.Header("Y")) + (1..20).map { index ->
                    FriendItem.Friend(
                        ComposeUserDisplay(user = ComposeUser(username = "friend-$index", nickname = "好友 $index")),
                    )
                }).toPersistentList()
                val settings = ComposeSetting(
                    ui = ComposeSetting.UIConfig(style = style, theme = if (dark) SettingTheme.DARK else SettingTheme.LIGHT),
                )
                val scene = ImageComposeScene(width = 390, height = 780, density = Density(1f)) {
                    CompositionLocalProvider(
                        LocalSharedState provides SharedState(settings = settings),
                        LocalInspectionMode provides true,
                        LocalFloatingContentBottomPadding provides 104.dp,
                    ) {
                        BgmAppTheme(modifier = Modifier.fillMaxSize()) {
                            FriendScreenContent(
                                state = FriendState(keys = persistentListOf("Y"), friends = friends),
                                onUiEvent = {},
                                onActionEvent = {},
                                listState = listState,
                            )
                        }
                    }
                }
                try {
                    repeat(24) { scene.render(it * 16_666_667L).close() }
                    assertEquals(780, listState.layoutInfo.viewportSize.height)
                    assertEquals(104, listState.layoutInfo.afterContentPadding)
                    val last = listState.layoutInfo.visibleItemsInfo.first { it.key == "friend-20" }
                    assertTrue(last.offset + last.size <= 780 - 104)
                    scene.render(400_000_000L).use { image ->
                        requireNotNull(image.encodeToData(EncodedImageFormat.PNG)).use { encoded ->
                            val output = File("build/reports/miuix-visual/friends-$style-${if (dark) "dark" else "light"}.png")
                            output.parentFile.mkdirs()
                            output.writeBytes(encoded.bytes)
                        }
                    }
                } finally {
                    scene.close()
                }
            }
        }
    }
}
