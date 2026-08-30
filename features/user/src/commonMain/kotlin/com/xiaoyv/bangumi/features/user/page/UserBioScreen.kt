package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_copy_success
import com.xiaoyv.bangumi.core_resource.resources.profile_network_service
import com.xiaoyv.bangumi.core_resource.resources.user_bio_title
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.text.BgmLinkedText
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserBioScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    val summary by produceState(stringResource(Res.string.user_bio_title), state.user.bio) {
        value = state.user.bio.bbcodeToHtml()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = ContentMarginHalf)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(ContentMargin)
    ) {
        if (state.user.networkServices.isNotEmpty()) {
            SectionTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                text = stringResource(Res.string.profile_network_service),
                showMore = false
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin),
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
            ) {
                val actionHandler = LocalActionHandler.current
                val clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()
                val popupTipState = LocalPopupTipState.current

                state.user.networkServices.forEach { service ->
                    val hasLink = service.url.isNotBlank()

                    AssistChip(
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = remember(service.color) { parseHtmlHexColor(service.color) ?: Color.Unspecified },
                            labelColor = Color.White
                        ),
                        onClick = {
                            if (hasLink) {
                                actionHandler.openInBrowser(service.url + service.account)
                            } else {
                                scope.launch {
                                    clipboard.setClipEntry(System.createClipEntry(service.account))
                                    popupTipState.showToast(getString(Res.string.global_copy_success))
                                }
                            }
                        },
                        shape = CircleShape,
                        label = {
                            Text(
                                text = service.title + ": " + service.account,
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
                .padding(ContentMargin),
            text = summary
        )
    }
}
