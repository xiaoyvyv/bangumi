package com.xiaoyv.bangumi.ui.feature.setting.ui

import android.view.Menu
import android.view.MenuItem
import com.blankj.utilcode.util.AppUtils
import com.xiaoyv.bangumi.databinding.ActivitySettingUiBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import kotlinx.coroutines.delay

/**
 * Class: [UiConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class UiConfigActivity : BaseBindingActivity<ActivitySettingUiBinding>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.settingImageAnimation.bindBoolean(this, ConfigHelper::isImageAnimation)
        binding.settingImageCompress.bindBoolean(this, ConfigHelper::isImageCompress)
        binding.settingGridAnimation.bindBoolean(this, ConfigHelper::isAdapterAnimation)
        binding.settingDynamicTheme.bindBoolean(this, ConfigHelper::isDynamicTheme, onChange = {
            launchUI {
                delay(500)
                AppUtils.relaunchApp(true)
            }
        })
        binding.settingFilterDelete.bindBoolean(this, ConfigHelper::isFilterDeleteComment)
        binding.settingBreakUp.bindBoolean(this, ConfigHelper::isFilterBreakUpComment)
    }

    override fun initListener() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "部分设置需要重启后生效", cancelText = null)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}