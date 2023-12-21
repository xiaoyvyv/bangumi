package com.xiaoyv.bangumi.ui.feature.setting.robot

import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingRobotBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [RobotConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class RobotConfigActivity : BaseBindingActivity<ActivitySettingRobotBinding>() {
    override fun initView() {
        binding.settingEnable.title = "班固米娘"
        binding.settingVoice.title = "触摸语音"

        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        refresh()
    }

    override fun initListener() {
        binding.settingEnable.setOnFastLimitClickListener {
            if (ConfigHelper.isRobotDisable) {
                showConfirmDialog(
                    message = "是否开启首页 Live2D 班固米娘？\n\n重启后生效",
                    onConfirmClick = {
                        ConfigHelper.isRobotDisable = false
                        refresh()
                    })
            } else {
                showConfirmDialog(
                    message = "是否关闭首页 Live2D 班固米娘？\n\n重启后生效",
                    onConfirmClick = {
                        ConfigHelper.isRobotDisable = true
                        refresh()
                    })
            }
        }

        binding.settingVoice.setOnFastLimitClickListener {
            if (ConfigHelper.isRobotVoiceDisable) {
                showConfirmDialog(
                    message = "是否开启首页 Live2D 班固米娘的触摸语音彩蛋？\n\n重启后生效",
                    onConfirmClick = {
                        ConfigHelper.isRobotVoiceDisable = false
                        refresh()
                    })
            } else {
                showConfirmDialog(
                    message = "是否关闭首页 Live2D 班固米娘触摸语音彩蛋？\n\n重启后生效",
                    onConfirmClick = {
                        ConfigHelper.isRobotVoiceDisable = true
                        refresh()
                    })
            }
        }
    }

    /**
     * 刷新 UI
     */
    private fun refresh() {
        binding.settingEnable.desc = if (ConfigHelper.isRobotDisable) "关闭" else "开启"
        binding.settingVoice.desc = if (ConfigHelper.isRobotVoiceDisable) "关闭" else "开启"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}