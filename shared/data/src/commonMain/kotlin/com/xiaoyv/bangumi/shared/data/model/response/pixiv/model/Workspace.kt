package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param pc
 * @param monitor
 * @param tool
 * @param scanner
 * @param tablet
 * @param mouse
 * @param printer
 * @param desktop
 * @param music
 * @param desk
 * @param chair
 * @param comment
 * @param workspaceImageUrl
 */
@Serializable

data class Workspace(

    @SerialName(value = "pc")
    val pc: String = "",

    @SerialName(value = "monitor")
    val monitor: String = "",

    @SerialName(value = "tool")
    val tool: String = "",

    @SerialName(value = "scanner")
    val scanner: String = "",

    @SerialName(value = "tablet")
    val tablet: String = "",

    @SerialName(value = "mouse")
    val mouse: String = "",

    @SerialName(value = "printer")
    val printer: String = "",

    @SerialName(value = "desktop")
    val desktop: String = "",

    @SerialName(value = "music")
    val music: String = "",

    @SerialName(value = "desk")
    val desk: String = "",

    @SerialName(value = "chair")
    val chair: String = "",

    @SerialName(value = "comment")
    val comment: String = "",

    @SerialName(value = "workspace_image_url")
    val workspaceImageUrl: String = ""

)

