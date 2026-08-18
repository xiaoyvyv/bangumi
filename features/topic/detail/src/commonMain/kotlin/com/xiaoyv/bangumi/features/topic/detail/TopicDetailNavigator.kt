package com.xiaoyv.bangumi.features.topic.detail

import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val topicDetailModule = module {
    viewModelOf(::TopicDetailViewModel)

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
