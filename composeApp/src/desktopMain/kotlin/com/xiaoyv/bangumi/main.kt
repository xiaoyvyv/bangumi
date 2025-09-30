package com.xiaoyv.bangumi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File

private val configFile = File(System.getProperty("user.dir"), "bgm.window_state")

fun main() = application {
    val savedState = remember { loadWindowStateFromFile() }
    val windowState = rememberWindowState(
        position = savedState.first,
        width = savedState.second.width,
        height = savedState.second.height,
    )

    LaunchedEffect(windowState) {
        snapshotFlow { Pair(windowState.position, windowState.size) }
            .distinctUntilChanged()
            .collectLatest { (position, size) ->
                saveWindowStateToFile(position, size)
            }
    }

    Window(
        onCloseRequest = {
            saveWindowStateToFile(windowState.position, windowState.size)
            exitApplication()
        },
        title = "Bangumi",
        alwaysOnTop = false,
        state = windowState
    ) {
        App()
    }
}


private fun saveWindowStateToFile(windowPosition: WindowPosition, windowSize: DpSize) {
    val x = windowPosition.x.value
    val y = windowPosition.y.value
    val width = windowSize.width.value
    val height = windowSize.height.value
    configFile.writeText("$x,$y,$width,$height")
}

private fun loadWindowStateFromFile(): Pair<WindowPosition, DpSize> {
    return try {
        val parts = configFile.readText().split(",")
        require(parts.size == 4)
        WindowPosition(
            x = parts[0].toFloat().dp,
            y = parts[1].toFloat().dp
        ) to DpSize(
            parts[2].toFloat().dp,
            parts[3].toFloat().dp,
        )
    } catch (e: Exception) {
        WindowPosition(Alignment.Center) to DpSize(1080.dp, 720.dp)
    }
}
