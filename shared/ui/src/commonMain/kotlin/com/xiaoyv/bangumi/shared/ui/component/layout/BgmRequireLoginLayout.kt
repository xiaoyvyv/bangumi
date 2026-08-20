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
import com.xiaoyv.bangumi.core_resource.resources.global_refresh
import com.xiaoyv.bangumi.core_resource.resources.login_first
import com.xiaoyv.bangumi.core_resource.resources.login_need_refresh
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Navigator
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform

@Composable
inline fun BgmRequireLogin(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    content: @Composable () -> Unit,
) {
    if (LocalSharedState.current.isLogin || !enable) {
        content()
    } else {
        BgmRequireLoginLayout(modifier)
    }
}

@Composable
fun BgmRequireLoginLayout(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ContentMargin, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val sharedState = LocalSharedState.current

        Text(text = stringResource(if (sharedState.isLogin) Res.string.login_need_refresh else Res.string.login_first))

        OutlinedButton(
            onClick = {
                if (sharedState.isLogin) {
                    onRefresh()
                } else {
                    KoinPlatform.getKoin().get<Navigator>().navigate(Screen.SignIn)
                }
            },
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Text(text = stringResource(if (sharedState.isLogin) Res.string.global_refresh else Res.string.global_login))
        }
    }
}
