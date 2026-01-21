package com.xiaoyv.bangumi.features.topic.detail

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val topicDetailModule = module {
    navScope {
        navigation<Screen.TopicDetail>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
            TopicDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}