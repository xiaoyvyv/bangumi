package com.xiaoyv.bangumi.features.detect.business

import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import androidx.lifecycle.SavedStateHandle
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.filekit.encodeToByteArray
import com.attafitamim.krop.filekit.toImageSrc
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.image_detect_action_copy_character
import com.xiaoyv.bangumi.core_resource.resources.image_detect_action_copy_subject
import com.xiaoyv.bangumi.core_resource.resources.image_detect_action_search_character
import com.xiaoyv.bangumi.core_resource.resources.image_detect_action_search_subject
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_model_anime
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_model_anime_1
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_model_anime_2
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_model_gal
import com.xiaoyv.bangumi.core_resource.resources.image_detect_character_subtitle
import com.xiaoyv.bangumi.core_resource.resources.image_detect_failed
import com.xiaoyv.bangumi.core_resource.resources.image_detect_subject
import com.xiaoyv.bangumi.core_resource.resources.image_detect_subject_subtitle
import com.xiaoyv.bangumi.shared.component.DetectType
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.repository.TraceRepository
import com.xiaoyv.bangumi.shared.ui.component.image.cropper.imageCropperCompat
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * [ReceiveViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class ReceiveViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.DetectImage,
    private val traceRepository: TraceRepository,
) : BaseViewModel<ReceiveState, ReceiveSideEffect, ReceiveEvent.Action>(savedStateHandle) {
    internal val imageCropper = imageCropperCompat()

    override fun createInitialState() =
        if (args.type == DetectType.SOURCE) ReceiveState(
            path = args.path,
            title = Res.string.image_detect_subject,
            subtitle = Res.string.image_detect_subject_subtitle,
            helpLink = "https://trace.moe"
        )
        else ReceiveState(
            path = args.path,
            title = Res.string.image_detect_character,
            subtitle = Res.string.image_detect_character_subtitle,
            helpLink = "https://www.animetrace.com",
            currentModel = "anime_model_lovelive",
            models = persistentMapOf(
                "anime_model_lovelive" to Res.string.image_detect_character_model_anime_1,
                "pre_stable" to Res.string.image_detect_character_model_anime_2,
                "anime" to Res.string.image_detect_character_model_anime,
                "full_game_model_kira" to Res.string.image_detect_character_model_gal
            ),
            itemActions = persistentListOf(
                ComposeTextTab(0, Res.string.image_detect_action_search_character),
                ComposeTextTab(1, Res.string.image_detect_action_search_subject),
                ComposeTextTab(2, Res.string.image_detect_action_copy_character),
                ComposeTextTab(3, Res.string.image_detect_action_copy_subject),
            )
        )

    override suspend fun Syntax<UiState<ReceiveState>, UiSideEffect<ReceiveSideEffect>>.refreshSync() {
        coroutineScope {
            launch { onLoadCropImage(PlatformFile(args.path)) }
            launch { onLoadAniTitleList() }
        }
    }

    override fun onEvent(event: ReceiveEvent.Action) {
        when (event) {
            is ReceiveEvent.Action.OnRefresh -> refresh(false)
            is ReceiveEvent.Action.OnRecognizingImageSource -> onRecognizingImage()
            is ReceiveEvent.Action.OnDismissResultDialog -> onDismissResultDialog()
            is ReceiveEvent.Action.OnSelectedFile -> onLoadCropImage(event.file)
            is ReceiveEvent.Action.OnChangeModel -> onChangeModel(event.model)
        }
    }

    private fun onChangeModel(model: String) = intent {
        reduceData { state.copy(currentModel = model) }
    }

    private fun onDismissResultDialog() = intent {
        reduceData {
            state.copy(
                resultSubject = persistentListOf(),
                resultCharacter = persistentListOf()
            )
        }
    }

    private fun onRecognizingImage() {
        when (args.type) {
            DetectType.SOURCE -> onRecognizingImageSource()
            DetectType.CHARACTER -> onRecognizingImageCharacter()
        }
    }

    private suspend fun onLoadAniTitleList() = subIntent {
        traceRepository.fetchAniTitleMapByEmbed().onSuccess { localMap ->
            reduceData { state.copy(titles = localMap.toPersistentList()) }

            traceRepository.fetchAniTitleMapByJsdelivr()
                .onFailure {
                    traceRepository.fetchAniTitleMapByGithub()
                        .onSuccess {
                            reduceData { state.copy(titles = localMap.toPersistentList()) }
                        }
                }
                .onSuccess {
                    reduceData { state.copy(titles = localMap.toPersistentList()) }
                }
        }
    }


    /**
     * 载入图片
     */
    private fun onLoadCropImage(file: PlatformFile) = intent {
        if (file.exists()) {
            imageCropper.load {
                file.toImageSrc()
            }
        }
    }

    /**
     * 识别动漫来源
     */
    private fun onRecognizingImageSource() = intent {
        reduceData {
            state.copy(
                isRecognizing = true,
                currentImageBitmap = null,
                resultSubject = persistentListOf()
            )
        }

        val result = imageCropper.save()
        if (result !is CropResult.Success) {
            reduceData { state.copy(isRecognizing = false) }
            postToast { getString(Res.string.image_detect_failed) }
            return@intent
        }

        traceRepository.fetchSubjectInfoFromImage(result.bitmap.encodeToByteArray())
            .onFailure {
                reduceData { state.copy(isRecognizing = false) }
                postToast { it.errMsg }
            }
            .onSuccess {
                reduceData {
                    state.copy(
                        isRecognizing = false,
                        resultSubject = it.result.orEmpty().toPersistentList(),
                        currentImageBitmap = result.bitmap
                    )
                }
            }
    }

    /**
     * 识别动漫人物
     */
    private fun onRecognizingImageCharacter() = intent {
        reduceData {
            state.copy(
                isRecognizing = true,
                resultCharacter = persistentListOf(),
                currentImageBitmap = null
            )
        }

        val result = imageCropper.save()
        if (result !is CropResult.Success) {
            reduceData { state.copy(isRecognizing = false) }
            postToast { getString(Res.string.image_detect_failed) }
            return@intent
        }

        traceRepository
            .fetchCharacterInfoFromImage(
                byteArray = result.bitmap.encodeToByteArray(),
                model = state.data.currentModel
            )
            .onFailure {
                reduceData { state.copy(isRecognizing = false) }
                postToast { it.errMsg }
            }
            .onSuccess {
                reduceData {
                    state.copy(
                        isRecognizing = false,
                        resultCharacter = it.data.orEmpty().toPersistentList(),
                        currentImageBitmap = result.bitmap
                    )
                }
            }
    }
}
