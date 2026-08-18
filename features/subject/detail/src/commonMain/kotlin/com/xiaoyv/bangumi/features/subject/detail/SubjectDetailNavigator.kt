package com.xiaoyv.bangumi.features.subject.detail

import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailViewModel
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailChartViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val subjectDetailModule = module {
    viewModelOf(::SubjectDetailViewModel)
    viewModel { (subjectId: Long) ->
        SubjectDetailChartViewModel(subjectRepository = get(), subjectId = subjectId)
    }

    navScope {
        navigation<Screen.SubjectDetail>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
            SubjectDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
