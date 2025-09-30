package com.xiaoyv.bangumi.shared.ui.kts

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_not_set
import org.jetbrains.compose.resources.stringResource

@Composable
fun String.orNotSet() = ifBlank { stringResource(Res.string.global_not_set) }