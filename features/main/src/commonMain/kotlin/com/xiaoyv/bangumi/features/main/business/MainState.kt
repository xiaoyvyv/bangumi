package com.xiaoyv.bangumi.features.main.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.shared.core.types.FeatureType
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainTabFeatureItems
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * [MainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MainState(
    val defaultSelected: Int = 0,
) {
    @Composable
    fun rememberBottomTabs(): PersistentList<Pair<Screen, ComposeVectorTab<String>>> {
        val settings = currentSettings()
        return remember(settings.homeTab) {
            val tabs = mainTabFeatureItems.entries.map { it.key to it.value }.toPersistentList()
            val items = mutableStateListOf<Pair<Screen, ComposeVectorTab<String>>>()
            val navTab1 = settings.homeTab.tab1.let { tabs.find { entry -> entry.second.type == it && it != FeatureType.TYPE_UNSET } }
            val navTab2 = settings.homeTab.tab2.let { tabs.find { entry -> entry.second.type == it && it != FeatureType.TYPE_UNSET } }
            val navTab3 = settings.homeTab.tab3.let { tabs.find { entry -> entry.second.type == it && it != FeatureType.TYPE_UNSET } }
            val navTab4 = settings.homeTab.tab4.let { tabs.find { entry -> entry.second.type == it && it != FeatureType.TYPE_UNSET } }
            val navTab5 = settings.homeTab.tab5.let { tabs.find { entry -> entry.second.type == it && it != FeatureType.TYPE_UNSET } }
            if (navTab1 != null) items.add(navTab1)
            if (navTab2 != null) items.add(navTab2)
            if (navTab3 != null) items.add(navTab3)
            if (navTab4 != null) items.add(navTab4)
            if (navTab5 != null) items.add(navTab5)

            // 全部隐藏了，强制显示 Profile Tab
            if (items.isEmpty()) persistentListOf(tabs.first { it.second.type == FeatureType.TYPE_PROFILE })
            else items.toPersistentList()
        }
    }
}
