package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.ui.text.input.TextFieldValue
import io.github.vinceglb.filekit.PlatformFile

sealed class CommentEvent {
    data class OnTextChange(val value: TextFieldValue) : CommentEvent()
    data class SendComment(val text: String) : CommentEvent()
    data class OnImagePickResult(val file: PlatformFile) : CommentEvent()
}
