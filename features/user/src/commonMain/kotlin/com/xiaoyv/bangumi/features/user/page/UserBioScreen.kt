package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.profile_network_service
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.utils.bbcodeToHtml
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserBioScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val summary by produceState(AnnotatedString("个人简介"), state.user.bio) {
        value = bbcodeToHtml(state.user.bio, true).parseAsHtml()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = LayoutPaddingHalf)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        if (state.user.networkServices.isNotEmpty()) {
            SectionTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                text = stringResource(Res.string.profile_network_service),
                showMore = false
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LayoutPadding),
                horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                val actionHandler = LocalActionHandler.current

                state.user.networkServices.forEach {
                    AssistChip(
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = remember(it.color) { parseHtmlHexColor(it.color) ?: Color.Unspecified },
                            labelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { actionHandler.openInBrowser(it.url) },
                        shape = CircleShape,
                        label = {
                            Text(
                                text = it.title + ": " + it.account,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        BgmLinkedText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LayoutPadding),
            text = summary,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

