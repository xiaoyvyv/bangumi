package com.xiaoyv.common.kts

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * ToolbarKt
 *
 * @author why
 * @since 11/19/23
 */
fun Toolbar.initNavBack(activity: AppCompatActivity, back: Boolean = true) {
    activity.setSupportActionBar(this)
    if (back) {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

fun MenuItem.initNavBack(activity: AppCompatActivity) {
    when (itemId) {
        android.R.id.home -> {
            activity.finish()
        }
    }
}