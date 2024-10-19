package com.xiaoyv.common.helper

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.widget.kts.showToastCompat

/**
 * 添加复制公共菜单
 */
fun Menu.addCommonMenu(url: String) {
    add(StringUtils.getString(CommonString.common_share))
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            val intent = IntentUtils.getShareTextIntent(url)
            ActivityUtils.startActivity(
                Intent.createChooser(
                    intent,
                    StringUtils.getString(CommonString.common_share)
                )
            )
            true
        }
    add(StringUtils.getString(CommonString.common_copy_link))
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            ClipboardUtils.copyText(url)
            showToastCompat(StringUtils.getString(CommonString.common_copy_success))
            true
        }
    add(StringUtils.getString(CommonString.common_open_browser))
        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
        .setOnMenuItemClickListener {
            openInBrowser(url)
            true
        }
}