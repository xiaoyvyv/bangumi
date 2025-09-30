package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_login
import com.xiaoyv.bangumi.core_resource.resources.login_first
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun BgmRequireLoginLayout(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    isLogin: Boolean = false,
    content: @Composable () -> Unit,
) {
    if (isLogin) content() else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(LayoutPadding, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(Res.string.login_first))
            OutlinedButton(onClick = onLogin, contentPadding = PaddingValues(horizontal = 24.dp)) {
                Text(text = stringResource(Res.string.global_login))
            }
        }
    }
}