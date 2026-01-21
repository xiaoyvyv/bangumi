package com.xiaoyv.bangumi.features.sign.sign_in

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_email
import com.xiaoyv.bangumi.core_resource.resources.global_load_error
import com.xiaoyv.bangumi.core_resource.resources.global_login
import com.xiaoyv.bangumi.core_resource.resources.global_password
import com.xiaoyv.bangumi.core_resource.resources.global_timeout
import com.xiaoyv.bangumi.core_resource.resources.login_email
import com.xiaoyv.bangumi.core_resource.resources.login_no_account
import com.xiaoyv.bangumi.core_resource.resources.login_password
import com.xiaoyv.bangumi.core_resource.resources.login_result_error
import com.xiaoyv.bangumi.core_resource.resources.login_result_known
import com.xiaoyv.bangumi.core_resource.resources.login_result_tip
import com.xiaoyv.bangumi.core_resource.resources.login_signup_now
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.core_resource.resources.login_verify_code
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInEvent
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInSideEffect
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInState
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.button.LoadingButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.adaptive.AdaptiveLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SignInRoute(
    viewModel: SignInViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val loginSuccessDialogState = rememberAlertDialogState(
        DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )

    viewModel.collectBaseSideEffect {
        when (it) {
            is SignInSideEffect.OnLoginResult -> {
                loginSuccessDialogState.show()
            }
        }
    }

    SignInScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SignInEvent.UI.OnNavUp -> onNavUp()
            }
        },
    )

    baseState.content {
        SignInDialogs(
            state = this,
            loginSuccessDialogState = loginSuccessDialogState,
            onActionEvent = viewModel::onEvent,
            onNavUp = onNavUp
        )
    }
}

@Composable
private fun SignInDialogs(
    state: SignInState,
    loginSuccessDialogState: AlertDialogState,
    onActionEvent: (SignInEvent.Action) -> Unit,
    onNavUp: () -> Unit,
) {
    BgmAlertDialog(
        state = loginSuccessDialogState,
        title = stringResource(if (state.loginResult.success) Res.string.login_result_tip else Res.string.login_result_error),
        text = state.loginResult.message.ifBlank { stringResource(Res.string.global_timeout) },
        cancel = null,
        confirm = stringResource(Res.string.login_result_known),
        onConfirm = {
            loginSuccessDialogState.dismiss()

            if (state.loginResult.success) onNavUp()
        }
    )
}

@Composable
private fun SignInScreen(
    baseState: BaseState<SignInState>,
    onUiEvent: (SignInEvent.UI) -> Unit,
    onActionEvent: (SignInEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.login_title),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SignInEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
            baseState = baseState,
        ) { state ->
            AdaptiveLayout(
                compat = {
                    SignInScreenContent(state, onUiEvent, onActionEvent)
                },
                other = {
                    SignInScreenContent(state, onUiEvent, onActionEvent)
                },
            )
        }
    }
}


@Composable
private fun SignInScreenContent(
    state: SignInState,
    onUiEvent: (SignInEvent.UI) -> Unit,
    onActionEvent: (SignInEvent.Action) -> Unit,
) {
    val emailFocusRequester = remember { FocusRequester() }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .semantics { contentDescription = "username" }
                .focusRequester(emailFocusRequester)
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = contentMargin),
            value = state.email,
            onValueChange = { onActionEvent(SignInEvent.Action.OnEmailChange(it)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            placeholder = { Text(text = stringResource(Res.string.global_email)) },
            label = { Text(text = stringResource(Res.string.login_email)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .semantics { contentDescription = "password" }
                .fillMaxWidth()
                .padding(horizontal = contentMargin),
            value = state.password,
            onValueChange = { onActionEvent(SignInEvent.Action.OnPasswordChange(it)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = { Text(text = stringResource(Res.string.global_password)) },
            label = { Text(text = stringResource(Res.string.login_password)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) BgmIcons.Visibility else BgmIcons.VisibilityOff,
                        contentDescription = stringResource(Res.string.login_password)
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = contentMargin),
            value = state.code,
            onValueChange = { onActionEvent(SignInEvent.Action.OnCodeChange(it)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            placeholder = { Text(text = stringResource(Res.string.login_verify_code)) },
            label = { Text(text = stringResource(Res.string.login_verify_code)) },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(40.dp)
                        .aspectRatio(3f)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable {
                            if (state.codeState !is LoadingState.Loading) {
                                onActionEvent(SignInEvent.Action.OnCodeChange(TextFieldValue()))
                                onActionEvent(SignInEvent.Action.OnRefreshVerifyCode)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        // 验证码加载中
                        state.codeState is LoadingState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        // 验证码加载失败
                        state.codeState is LoadingState.Error -> {
                            Text(
                                text = stringResource(Res.string.global_load_error),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        // 验证码加载完成
                        state.codeImage.isNotEmpty() -> AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = state.codeImage,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = stringResource(Res.string.login_verify_code)
                        )
                    }
                }
            }
        )

        Column {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = contentMargin)
                    .padding(top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = stringResource(Res.string.login_no_account)
            )

            Row(modifier = Modifier.padding(horizontal = contentMargin)) {
                TextButton(
                    modifier = Modifier.offset(x = (-12).dp),
                    onClick = { onActionEvent(SignInEvent.Action.OnSignIn) }
                ) {
                    Text(
                        text = stringResource(Res.string.login_signup_now),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LoadingButton(
                    loading = state.loggingRunning,
                    enabled = state.enableLogin,
                    contentPadding = PaddingValues(horizontal = 36.dp),
                    onClick = {
                        keyboardController?.hide()
                        onActionEvent(SignInEvent.Action.OnSignIn)
                    }
                ) {
                    Text(text = stringResource(Res.string.global_login))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }
}

