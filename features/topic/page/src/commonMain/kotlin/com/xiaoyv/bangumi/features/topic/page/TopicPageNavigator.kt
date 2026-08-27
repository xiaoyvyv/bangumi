package com.xiaoyv.bangumi.features.topic.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.topic.page.business.TopicPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.jetbrains.compose.resources.stringResource
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val topicPageModule = module {
    viewModel { (param: ListTopicParam) ->
        TopicPageViewModel(param = param, topicRepository = get())
    }

    navScope {
        navigation<Screen.TopicPage> { key ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    BgmTopAppBar(
                        title = stringResource(key.param.title),
                        onNavigationClick = { navigator.goBack() }
                    )
                },
            ) {
                TopicPageRoute(
                    param = key.param,
                    onNavScreen = { navigator.navigate(it) }
                )
            }
        }
    }
}
