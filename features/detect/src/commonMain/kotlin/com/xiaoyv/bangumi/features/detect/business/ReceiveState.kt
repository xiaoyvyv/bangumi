package com.xiaoyv.bangumi.features.detect.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceName
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.StringResource

/**
 * [ReceiveState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class ReceiveState(
    val title: StringResource,
    val subtitle: StringResource,
    val path: String = "",
    val helpLink: String = "",
    val titles: SerializeList<ComposeTraceName> = persistentListOf(),

    val isRecognizing: Boolean = false,
    val models: SerializeMap<String, StringResource> = persistentMapOf(),
    val currentModel: String = "pre_stable",
    val currentImageBitmap: ImageBitmap? = null,
    val resultSubject: SerializeList<ComposeTraceMoe.Result> = persistentListOf(),
    val resultCharacter: SerializeList<ComposeTraceCharacter.Data> = persistentListOf(),
    val itemActions: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
)
