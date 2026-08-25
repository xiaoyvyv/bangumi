package com.xiaoyv.bangumi.features.settings.live2d.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_auto
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_black_musume
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_musume
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_100
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_150
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_200
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_50
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [SettingsLive2dState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SettingsLive2dState(
    val title: String = "",
    val shellItems: SerializeList<ComposeTextTab<Int>> = persistentListOf(
        ComposeTextTab(ComposeSetting.Live2dConfig.Shell.AUTO, Res.string.settings_live2d_shell_auto),
        ComposeTextTab(ComposeSetting.Live2dConfig.Shell.MUSUME, Res.string.settings_live2d_shell_musume),
        ComposeTextTab(ComposeSetting.Live2dConfig.Shell.BLACK_MUSUME, Res.string.settings_live2d_shell_black_musume),
    ),
    val sizeItems: SerializeList<ComposeTextTab<Int>> = persistentListOf(
        ComposeTextTab(ComposeSetting.Live2dConfig.Size.SIZE_50, Res.string.settings_live2d_size_50),
        ComposeTextTab(ComposeSetting.Live2dConfig.Size.SIZE_100, Res.string.settings_live2d_size_100),
        ComposeTextTab(ComposeSetting.Live2dConfig.Size.SIZE_150, Res.string.settings_live2d_size_150),
        ComposeTextTab(ComposeSetting.Live2dConfig.Size.SIZE_200, Res.string.settings_live2d_size_200),
    ),
)
