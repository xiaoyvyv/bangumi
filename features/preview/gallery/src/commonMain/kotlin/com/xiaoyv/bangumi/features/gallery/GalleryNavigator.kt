package com.xiaoyv.bangumi.features.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navInt
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_GALLERY
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


data class GalleryArguments(
    val id: String,
    @field:ListAlbumType val type: Int,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.get<String>("id").orEmpty(),
        type = savedStateHandle.get<Int>("type") ?: ListAlbumType.UNKNOWN,
    )
}

fun NavHostController.navigateGallery(screen: Screen.Gallery) = debounce(screen.route) {
    navigateByRoute(screen.route, "id" to screen.id, "type" to screen.type)
}

fun NavGraphBuilder.addGalleryScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(SCREEN_ROUTE_GALLERY, navString("id"), navInt("type")) {
        GalleryRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}