@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report
import com.xiaoyv.bangumi.core_resource.resources.global_copy_link
import com.xiaoyv.bangumi.core_resource.resources.global_netabare
import com.xiaoyv.bangumi.core_resource.resources.global_open_browser
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.global_share
import org.jetbrains.compose.resources.StringResource

enum class ButtonType(val label: StringResource) {
    Share(Res.string.global_share),
    Report(Res.string.action_report),
    OpenInBrowser(Res.string.global_open_browser),
    CopyLink(Res.string.global_copy_link),
    Netabare(Res.string.global_netabare),
    Reaction(Res.string.global_reaction);

    fun contentColor(colorScheme: ColorScheme): Color {
        return when (this) {
            Report -> colorScheme.error
            else -> colorScheme.onSurface
        }
    }
}