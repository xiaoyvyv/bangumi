package com.xiaoyv.bangumi.ui.feature.setting.robot

import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingRobotBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.initNavBack

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
        binding.settingEnable.bindBoolean(this, ConfigHelper::isRobotEnable, dialogTip = {
            "是否${if (it) "关闭" else "开启"}首页 Live2D 班固米娘？\n\n重启后生效"
        })
        binding.settingVoice.bindBoolean(this, ConfigHelper::isRobotVoiceEnable, dialogTip = {
            "是否${if (it) "关闭" else "开启"}首页 Live2D 班固米娘的触摸语音彩蛋？\n\n重启后生效"
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}