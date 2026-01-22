package com.xiaoyv.bangumi.features.sign.sign_in.business

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.LoginParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult
import com.xiaoyv.bangumi.shared.data.repository.SignRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.toImmutableMap

/**
 * [SignInViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SignInViewModel(
    private val signRepo: SignRepository,
    private val userManager: UserManager,
    private val userRepository: UserRepository,
    stateHandle: SavedStateHandle,
) : BaseViewModel<SignInState, SignInSideEffect, SignInEvent.Action>(stateHandle) {

    override fun initSate(onCreate: Boolean) = SignInState()

    override suspend fun BaseSyntax<SignInState, SignInSideEffect>.refreshSync() {
        onRefreshVerifyCodeImage()
    }

    override fun onEvent(event: SignInEvent.Action) {
        when (event) {
            is SignInEvent.Action.OnEmailChange -> onEmailChange(event.email)
            is SignInEvent.Action.OnPasswordChange -> onPasswordChange(event.password)
            is SignInEvent.Action.OnCodeChange -> onCodeChange(event.code)
            is SignInEvent.Action.OnSignIn -> onSignIn()
            is SignInEvent.Action.OnRefreshVerifyCode -> onRefreshVerifyCodeImage()
        }
    }

    private fun onCodeChange(value: TextFieldValue) = action {
        reduceContent { state.copy(code = value) }
    }

    private fun onPasswordChange(value: TextFieldValue) = action {
        reduceContent { state.copy(password = value) }
    }

    private fun onEmailChange(value: TextFieldValue) = action {
        reduceContent { state.copy(email = value) }
    }

    private fun onRefreshVerifyCodeImage() = action {
        reduceContent {
            state.copy(
                codeState = LoadingState.Loading,
                codeImage = byteArrayOf(),
                code = TextFieldValue()
            )
        }

        // 拉取登录表单
        signRepo.fetchLoginForm()
            .onFailure {
                postToast { it.errMsg }

                reduceContent {
                    state.copy(
                        codeState = LoadingState.Error(it),
                        codeImage = byteArrayOf(),
                        code = TextFieldValue()
                    )
                }
            }
            .onSuccess { loginForm ->
                // 当前用户已经登录成功，无需再登录
                if (loginForm.hasLogin && loginForm.loginInfo != ComposeLoginResult.Empty && loginForm.loginInfo.success) {
                    reduceContent { state.copy(codeState = LoadingState.NotLoading) }

                    onSaveUser(loginForm.loginInfo)
                } else {
                    signRepo.fetchVerifyCodeImage()
                        .onFailure {
                            reduceContent {
                                state.copy(
                                    codeState = LoadingState.Error(it),
                                    codeImage = byteArrayOf(),
                                    code = TextFieldValue()
                                )
                            }
                        }
                        .onSuccess {
                            reduceContent {
                                state.copy(
                                    codeState = LoadingState.NotLoading,
                                    codeImage = it,
                                    code = TextFieldValue(),
                                    otherForms = loginForm.forms.toImmutableMap()
                                )
                            }
                        }
                }
            }
    }

    private fun onSignIn() = action {
        reduceContent { state.copy(loggingRunning = true) }

        signRepo.sendLogin(
            param = state.content.let {
                LoginParam(
                    email = it.email.text,
                    password = it.password.text,
                    code = it.code.text,
                    otherForms = it.otherForms
                )
            }
        ).onFailure {
            postToast { it.errMsg }

            reduceContent {
                state.copy(
                    loggingRunning = false,
                    loginResult = ComposeLoginResult.Empty,
                    code = TextFieldValue(),
                    codeImage = byteArrayOf()
                )
            }

            // 刷新验证码
            onRefreshVerifyCodeImage()
        }.onSuccess {
            // 登录成功保存用户
            if (it.success) onSaveUser(it) else {
                // 刷新验证码
                onRefreshVerifyCodeImage()

                // 登录失败了
                reduceContent {
                    state.copy(
                        loggingRunning = false,
                        code = TextFieldValue(),
                        codeImage = byteArrayOf(),
                        loginResult = it
                    )
                }

                postEffect { SignInSideEffect.OnLoginResult(it) }
            }
        }
    }

    /**
     * 获取授权，保存用户信息
     */
    private fun onSaveUser(loginInfo: ComposeLoginResult) = action {
        runCatching {
            val user = loginInfo.composeUser
            val token = userRepository.submitRequestToken(user.formHash).getOrThrow()
            val info = userRepository.fetchUserInfo(user.username).getOrThrow()
            user.copy(id = info.id, group = info.group, sign = info.sign) to token
        }.onFailure {
            reduceContent { state.copy(loggingRunning = false, loginResult = ComposeLoginResult.Empty) }
            postToast { it.errMsg }
        }.onSuccess {
            val (userInfo, userToken) = it

            // 保存用户信息
            userManager.login(userInfo, userToken)

            reduceContent { state.copy(loggingRunning = false, loginResult = loginInfo) }

            postEffect { SignInSideEffect.OnLoginResult(loginInfo) }

            debugLog { "登录用户: $userInfo, token: $userToken" }
        }
    }
}