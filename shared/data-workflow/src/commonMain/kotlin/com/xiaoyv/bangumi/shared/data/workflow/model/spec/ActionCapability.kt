package com.xiaoyv.bangumi.shared.data.workflow.model.spec

/**
 * 工作流需要向用户声明的高风险能力。
 */
object ActionCapability {
    const val OPEN_EXTERNAL_URL = "open_external_url"
    const val OPEN_EXTERNAL_APP = "open_external_app"
    const val OPEN_INTERNAL_WEB = "open_internal_web"
    const val NETWORK = "network"
    const val NETWORK_LOCAL_COOKIE_ACCESS = "network_local_cookie_access"
    const val NETWORK_COOKIE_SYNC = "network_cookie_sync"
    const val CLIPBOARD_WRITE = "clipboard_write"
    const val CONFIRM_DIALOG = "confirm_dialog"
    const val INPUT_DIALOG = "input_dialog"
    const val SELECT_DIALOG = "select_dialog"
    const val PROGRESS_DIALOG = "progress_dialog"
    const val SHARE = "share"
    const val NOTIFICATION = "notification"
    const val IMAGE_PREVIEW = "image_preview"
    const val VIDEO_PREVIEW = "video_preview"
}
