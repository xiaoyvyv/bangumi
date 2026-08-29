@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report
import com.xiaoyv.bangumi.core_resource.resources.global_add_to_index
import com.xiaoyv.bangumi.core_resource.resources.global_copy
import com.xiaoyv.bangumi.core_resource.resources.global_copy_link
import com.xiaoyv.bangumi.core_resource.resources.global_copy_name
import com.xiaoyv.bangumi.core_resource.resources.global_copy_name_cn
import com.xiaoyv.bangumi.core_resource.resources.global_delete
import com.xiaoyv.bangumi.core_resource.resources.global_netabare
import com.xiaoyv.bangumi.core_resource.resources.global_open_browser
import com.xiaoyv.bangumi.core_resource.resources.global_reaction
import com.xiaoyv.bangumi.core_resource.resources.global_share
import com.xiaoyv.bangumi.core_resource.resources.global_sort_asc
import com.xiaoyv.bangumi.core_resource.resources.global_sort_desc
import com.xiaoyv.bangumi.core_resource.resources.global_translate
import com.xiaoyv.bangumi.core_resource.resources.global_translate_off
import org.jetbrains.compose.resources.StringResource

enum class ButtonType(val label: StringResource) {
    Share(Res.string.global_share),
    Report(Res.string.action_report),
    OpenInBrowser(Res.string.global_open_browser),
    CopyLink(Res.string.global_copy_link),
    Netabare(Res.string.global_netabare),
    AddToIndex(Res.string.global_add_to_index),
    Translate(Res.string.global_translate),
    TranslateOff(Res.string.global_translate_off),
    Reaction(Res.string.global_reaction),
    Copy(Res.string.global_copy),
    CopyName(Res.string.global_copy_name),
    CopyNameCn(Res.string.global_copy_name_cn),
    SortDesc(Res.string.global_sort_desc),
    SortAsc(Res.string.global_sort_asc),
    Delete(Res.string.global_delete);

    fun contentColor(colorScheme: ColorScheme): Color {
        return when (this) {
            Report -> colorScheme.error
            Delete -> colorScheme.error
            else -> colorScheme.onSurface
        }
    }
}