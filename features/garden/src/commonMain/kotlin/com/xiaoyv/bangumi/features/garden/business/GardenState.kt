package com.xiaoyv.bangumi.features.garden.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.data.model.request.SearchMagnetBody

/**
 * [GardenState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class GardenState(
    val query: TextFieldValue = TextFieldValue(),
    val param: SearchMagnetBody = SearchMagnetBody.Empty,
)
