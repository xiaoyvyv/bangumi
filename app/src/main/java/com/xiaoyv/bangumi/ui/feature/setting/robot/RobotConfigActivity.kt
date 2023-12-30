package com.xiaoyv.bangumi.ui.feature.setting.robot

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingRobotBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog

/**
 * Class: [RobotConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class RobotConfigActivity : BaseBindingActivity<ActivitySettingRobotBinding>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.settingEnable.bindBoolean(ConfigHelper::isRobotEnable)
        binding.settingVoice.bindBoolean(ConfigHelper::isRobotVoiceEnable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "设置需要重启后生效", cancelText = null)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}