package com.xiaoyv.common.helper

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.IntentUtils
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.widget.kts.showToastCompat

/**
 * 添加复制公共菜单
 */
fun Menu.addCommonMenu(url: String) {
    add("分享")
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            val intent = IntentUtils.getShareTextIntent(url)
            ActivityUtils.startActivity(Intent.createChooser(intent, "分享"))
            true
        }
    add("复制链接")
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            ClipboardUtils.copyText(url)
            showToastCompat("复制成功")
            true
        }
    add("在浏览器中打开")
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            openInBrowser(url)
            true
        }
}