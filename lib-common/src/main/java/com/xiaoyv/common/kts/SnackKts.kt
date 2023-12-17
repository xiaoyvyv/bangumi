package com.xiaoyv.common.kts

import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SnackbarUtils

/**
 * @author why
 * @since 12/17/23
 */
fun Fragment.showSnackBar(
    message: String,
    duration: Int = SnackbarUtils.LENGTH_SHORT,
    error: Boolean = false,
) {
    hideSnackBar()

    if (error) {
        SnackbarUtils.with(requireView())
            .setMessage(message)
            .setDuration(duration)
            .showError()
    } else {
        SnackbarUtils.with(requireView())
            .setMessage(message)
            .setDuration(duration)
            .show()
    }
}

fun hideSnackBar() {
    SnackbarUtils.dismiss()
}
