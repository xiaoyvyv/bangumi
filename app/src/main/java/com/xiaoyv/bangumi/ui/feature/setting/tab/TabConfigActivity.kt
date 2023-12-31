package com.xiaoyv.bangumi.ui.feature.setting.tab

import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivitySettingTabBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [TabConfigActivity]
 *
 * @author why
 * @since 12/30/23
 */
class TabConfigActivity : BaseBindingActivity<ActivitySettingTabBinding>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        val names = listOf("隐藏", "发现", "时间胶囊", "排行榜", "追番进度", "超展开", "我的")
        val values = listOf(
            FeatureType.TYPE_UNSET,
            FeatureType.TYPE_DISCOVER,
            FeatureType.TYPE_TIMELINE,
            FeatureType.TYPE_RANK,
            FeatureType.TYPE_PROCESS,
            FeatureType.TYPE_RAKUEN,
            FeatureType.TYPE_PROFILE
        )

        binding.settingFirstTab.bindSerializable(
            activity = this,
            property = ConfigHelper::homeDefaultTab,
            names = listOf("Tab-1", "Tab-2", "Tab-3", "Tab-4", "Tab-5"),
            values = listOf(0, 1, 2, 3, 4)
        )

        binding.settingTab1.bindSerializable(
            activity = this,
            property = ConfigHelper::homeTab1,
            names = names,
            values = values
        )

        binding.settingTab2.bindSerializable(
            activity = this,
            property = ConfigHelper::homeTab2,
            names = names,
            values = values
        )

        binding.settingTab3.bindSerializable(
            activity = this,
            property = ConfigHelper::homeTab3,
            names = names,
            values = values
        )

        binding.settingTab4.bindSerializable(
            activity = this,
            property = ConfigHelper::homeTab4,
            names = names,
            values = values
        )

        binding.settingTab5.bindSerializable(
            activity = this,
            property = ConfigHelper::homeTab5,
            names = names,
            values = values
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showTip()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (ConfigHelper.homeTab1 == FeatureType.TYPE_PROFILE
                    || ConfigHelper.homeTab2 == FeatureType.TYPE_PROFILE
                    || ConfigHelper.homeTab3 == FeatureType.TYPE_PROFILE
                    || ConfigHelper.homeTab4 == FeatureType.TYPE_PROFILE
                    || ConfigHelper.homeTab5 == FeatureType.TYPE_PROFILE
                ) {
                    finish()
                } else {
                    showConfirmDialog(
                        message = SpanUtils.with(null)
                            .append("你的配置未保留我的TAB")
                            .setForegroundColor(getAttrColor(GoogleAttr.colorError))
                            .append("，配置需要重启后生效，是否确认更改？\n\n快捷打开设置页：\n")
                            .append("首页连续按下：音量+、音量+、音量-、音量-")
                            .setForegroundColor(getAttrColor(GoogleAttr.colorError))
                            .create(),
                        onConfirmClick = {
                            finish()
                        }
                    )
                }
            }
        })
    }

    private fun showTip() {
        showConfirmDialog(
            message = SpanUtils.with(null)
                .append("设置需要重启后生效，建议至少")
                .append("保留一个我的TAB")
                .setForegroundColor(getAttrColor(GoogleAttr.colorError))
                .append("，以便更改设置项，否则只能在首页通过按键打开设置页\n\n打开设置：\n")
                .append("首页连续按下：音量+、音量+、音量-、音量-")
                .setForegroundColor(getAttrColor(GoogleAttr.colorError))
                .create(),
            cancelText = null
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}