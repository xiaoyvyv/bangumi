package com.xiaoyv.bangumi.features.timeline.detail

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val timelineDetailModule = module {
    viewModelOf(::TimelineDetailViewModel)

    navScope {
        navigation<Screen.TimelineDetail>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
            TimelineDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
