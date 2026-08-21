package com.xiaoyv.bangumi.features.subject.detail

import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailViewModel
import com.xiaoyv.bangumi.features.subject.detail.page.chart.SubjectDetailChartViewModel
import com.xiaoyv.bangumi.features.subject.detail.page.rant.SubjectDetailRantViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val subjectDetailModule = module {
    viewModelOf(::SubjectDetailViewModel)
    viewModel { (subjectId: Long) ->
        SubjectDetailChartViewModel(subjectId = subjectId, subjectRepository = get())
    }
    viewModel { (subjectId: Long, collectType: Int) ->
        SubjectDetailRantViewModel(
            subjectId = subjectId,
            collectionType = collectType,
            subjectRepository = get(),
            topicRepository = get(),
            userManager = get()
        )
    }


    navScope {
        navigation<Screen.SubjectDetail> { key ->
            SubjectDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
