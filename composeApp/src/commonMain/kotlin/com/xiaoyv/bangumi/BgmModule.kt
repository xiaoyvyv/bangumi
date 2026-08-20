package com.xiaoyv.bangumi

import com.xiaoyv.bangumi.shared.data.di.dataModules
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedViewModel
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogAnchor
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun KoinApplication.initializeKoin() = modules(
    commonModule,
    navigationModule,
    *dataModules
)

val commonModule = module {
    viewModelOf(::SharedViewModel)
    viewModel { (anchor: CommentDialogAnchor) ->
        CommentViewModel(
            dialogAnchor = anchor,
            choreRepository = get(),
            topicRepository = get(),
            monoRepository = get(),
            blogRepository = get(),
            indexRepository = get(),
        )
    }
}
