package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.subject_action_more
import com.xiaoyv.bangumi.features.blog.page.BlogPageRoute
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import org.jetbrains.compose.resources.stringResource

/**
 * [SubjectDetailBlogScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailBlogScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        BlogPageRoute(
            param = remember { ListBlogParam(type = ListBlogType.SUBJECT_RELATED, subjectId = state.id) },
            onNavScreen = {
                onUiEvent(SubjectDetailEvent.UI.OnNavScreen(it))
            }
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(ContentMargin),
            onClick = {
                onUiEvent(
                    SubjectDetailEvent.UI.OnNavScreen(
                        Screen.PublishMain(
                            type = PublishPostType.BLOG,
                            publishAttachId = state.subject.id.toString()
                        )
                    )
                )
            }
        ) {
            Icon(
                imageVector = BgmIcons.Edit,
                contentDescription = stringResource(Res.string.subject_action_more)
            )
        }
    }
}