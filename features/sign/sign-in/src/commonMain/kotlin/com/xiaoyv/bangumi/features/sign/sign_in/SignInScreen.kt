package com.xiaoyv.bangumi.features.sign.sign_in

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.xiaoyv.bangumi.core_resource.resources.login_verify_code
import com.xiaoyv.bangumi.core_resource.resources.login_welcome
import com.xiaoyv.bangumi.core_resource.resources.login_welcome_tip
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInEvent
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInSideEffect
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInState
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.button.LoadingButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.adaptive.AdaptiveLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SignInEvent.UI.OnNavUp -> onNavUp()
            }
        },
    )

    baseState.data.run {
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
    uiState: UiState<SignInState>,
    onUiEvent: (SignInEvent.UI) -> Unit,
    onActionEvent: (SignInEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            BgmTopAppBar(
                onNavigationClick = { onUiEvent(SignInEvent.UI.OnNavUp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                ),
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to MaterialTheme.colorScheme.surface,
                        0.48f to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f),
                        1f to MaterialTheme.colorScheme.surface,
                    )
                ),
            uiState = uiState,
        ) { state ->
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = it.calculateBottomPadding()),
            ) {
                AdaptiveLayout(
                    compat = {
                        SignInScreenContent(
                            state = state,
                            maxWidth = 520.dp,
                            topPadding = it.calculateTopPadding(),
                            onActionEvent = onActionEvent,
                            scrollState = scrollState,
                        )
                    },
                    other = {
                        SignInScreenContent(
                            state = state,
                            scrollState = scrollState,
                            maxWidth = 600.dp,
                            topPadding = it.calculateTopPadding(),
                            onActionEvent = onActionEvent,
                        )
                    },
                )
            }
        }
    }
}


@Composable
private fun SignInScreenContent(
    state: SignInState,
    maxWidth: Dp,
    topPadding: Dp,
    onActionEvent: (SignInEvent.Action) -> Unit,
    scrollState: ScrollState,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val fieldShape = MaterialTheme.shapes.largeIncreased
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)


    Column(modifier = Modifier.fillMaxWidth()) {
        SignInHero(topPadding)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-28).dp),
                    shape = MaterialTheme.shapes.largeIncreased,
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .semantics { contentDescription = "username" }
                                .fillMaxWidth(),
                            value = state.email,
                            onValueChange = { onActionEvent(SignInEvent.Action.OnEmailChange(it)) },
                            singleLine = true,
                            shape = fieldShape,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                            placeholder = { Text(text = stringResource(Res.string.global_email)) },
                            label = { Text(text = stringResource(Res.string.login_email)) }
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .semantics { contentDescription = "password" }
                                .fillMaxWidth(),
                            value = state.password,
                            onValueChange = { onActionEvent(SignInEvent.Action.OnPasswordChange(it)) },
                            singleLine = true,
                            shape = fieldShape,
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
                        val scope = rememberCoroutineScope()
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth()
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        scope.launch { scrollState.animateScrollTo(300) }
                                    }
                                },
                            value = state.code,
                            onValueChange = { onActionEvent(SignInEvent.Action.OnCodeChange(it)) },
                            singleLine = true,
                            shape = fieldShape,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (state.enableLogin) {
                                        keyboardController?.hide()
                                        onActionEvent(SignInEvent.Action.OnSignIn)
                                    }
                                }
                            ),
                            placeholder = { Text(text = stringResource(Res.string.login_verify_code)) },
                            label = { Text(text = stringResource(Res.string.login_verify_code)) },
                            trailingIcon = {
                                VerifyCode(
                                    state = state,
                                    onActionEvent = onActionEvent,
                                )
                            }
                        )

                        LoadingButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 54.dp),
                            loading = state.loggingRunning,
                            enabled = state.enableLogin,
                            shape = MaterialTheme.shapes.largeIncreased,
                            contentPadding = ButtonDefaults.ContentPadding,
                            onClick = {
                                keyboardController?.hide()
                                onActionEvent(SignInEvent.Action.OnSignIn)
                            }
                        ) {
                            Text(
                                text = stringResource(Res.string.global_login),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                val uriHandler = LocalUriHandler.current
                Row(
                    modifier = Modifier.offset(y = (-10).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.login_no_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { uriHandler.openUri(WebConstant.URL_BGM_SIGN) }) {
                        Text(
                            text = stringResource(Res.string.login_signup_now),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        // Keep the focused field scrollable above the software keyboard.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Composable
private fun SignInHero(topPadding: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(topPadding + 156.dp)
            .clip(
                RoundedCornerShape(
                    bottomEnd = 44.dp,
                    bottomStart = 14.dp,
                )
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 42.dp, y = (-44).dp)
                .size(142.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    CircleShape,
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 24.dp, y = 30.dp)
                .size(92.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.48f),
                    CircleShape,
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(
                    start = ContentMargin,
                    top = topPadding + 4.dp,
                    end = 88.dp,
                    bottom = 18.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "BANGUMI",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.4.sp,
            )
            Text(
                text = stringResource(Res.string.login_welcome),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = stringResource(Res.string.login_welcome_tip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun VerifyCode(
    state: SignInState,
    onActionEvent: (SignInEvent.Action) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .height(40.dp)
            .aspectRatio(3f)
            .clip(RoundedCornerShape(10.dp))
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
            state.codeState is LoadingState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }

            state.codeState is LoadingState.Error -> {
                Text(
                    text = stringResource(Res.string.global_load_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.codeImage.isNotEmpty() -> AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = state.codeImage,
                contentScale = ContentScale.FillBounds,
                contentDescription = stringResource(Res.string.login_verify_code)
            )
        }
    }
}


@Preview
@Composable
fun PreviewSignInScreen() {
    PreviewColumn {
        SignInScreen(
            uiState = UiState(SignInState()),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}
