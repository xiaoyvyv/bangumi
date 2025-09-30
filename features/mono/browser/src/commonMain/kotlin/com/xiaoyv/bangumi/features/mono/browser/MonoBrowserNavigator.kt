package com.xiaoyv.bangumi.features.mono.browser

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.getInt
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.core.utils.toJson
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_OBJ
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TYPE
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.MonoBrowserRouteDefinition
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64

data class MonoBrowserArguments(
    @field:MonoType val type: Int,
    val param: MonoBrowserBody,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        type = savedStateHandle.getInt(EXTRA_TYPE, MonoType.UNKNOWN),
        param = savedStateHandle.getString(EXTRA_OBJ)
            .decodeBase64String()
            .fromJson<MonoBrowserBody>() ?: MonoBrowserBody.Empty
    )
}

fun NavHostController.navigateMonoBrowser(screen: Screen.MonoBrowser) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_TYPE, screen.type)
        param(EXTRA_OBJ, screen.param.toJson().encodeBase64())
    }
}

fun NavGraphBuilder.addMonoBrowserScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(definition = MonoBrowserRouteDefinition) {
        MonoBrowserRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}