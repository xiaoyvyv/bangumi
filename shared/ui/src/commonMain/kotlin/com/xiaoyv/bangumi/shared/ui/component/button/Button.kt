package com.xiaoyv.bangumi.shared.ui.component.button

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoingContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoingText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDoneText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDroppedContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionDroppedText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionOnHoldContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionOnHoldText
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorCollectionWishText
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiredContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiredText
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiringContainer
import com.xiaoyv.bangumi.shared.ui.theme.colorStateAiringText

@Composable
fun collectionButtonColors(@CollectionType type: Int): ButtonColors = when (type) {
    CollectionType.WISH -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionWishText,
        containerColor = colorCollectionWishContainer
    )

    CollectionType.DOING -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionDoingText,
        containerColor = colorCollectionDoingContainer
    )

    CollectionType.DONE -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionDoneText,
        containerColor = colorCollectionDoneContainer
    )

    CollectionType.ASIDE -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionOnHoldText,
        containerColor = colorCollectionOnHoldContainer
    )

    CollectionType.DROP -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionDroppedText,
        containerColor = colorCollectionDroppedContainer
    )

    else -> ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun episodeCollectionButtonColors(
    @CollectionEpisodeType type: Int,
    isAiring: Boolean,
    isAired: Boolean,
): ButtonColors = when {
    // 看过
    type == CollectionEpisodeType.DONE -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionDoneText,
        containerColor = colorCollectionDoneContainer
    )
    // 想看
    type == CollectionEpisodeType.WISH -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionWishText,
        containerColor = colorCollectionWishContainer
    )
    // 抛弃
    type == CollectionEpisodeType.DROPPED -> ButtonDefaults.textButtonColors(
        contentColor = colorCollectionDroppedText,
        containerColor = colorCollectionDroppedContainer
    )

    isAiring -> ButtonDefaults.textButtonColors(
        contentColor = colorStateAiringText,
        containerColor = colorStateAiringContainer,
    )

    isAired -> ButtonDefaults.textButtonColors(
        contentColor = colorStateAiredText,
        containerColor = colorStateAiredContainer
    )

    else -> ButtonDefaults.textButtonColors(
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
}