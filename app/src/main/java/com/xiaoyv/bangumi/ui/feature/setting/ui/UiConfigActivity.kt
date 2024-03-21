package com.xiaoyv.bangumi.ui.feature.setting.ui

import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.google.android.material.color.DynamicColors
import com.xiaoyv.bangumi.databinding.ActivitySettingUiBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.theme.ThemeHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog

/**
 * Class: [UiConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class UiConfigActivity : BaseBindingActivity<ActivitySettingUiBinding>() {

    private val themeNames by lazy {
        if (DynamicColors.isDynamicColorAvailable()) listOf("明亮", "暗黑", "跟随系统", "跟随壁纸")
        else listOf("明亮", "暗黑", "跟随系统")
    }

    private val themeValues by lazy {
        if (DynamicColors.isDynamicColorAvailable()) listOf(0, 1, 2, 3)
        else listOf(0, 1, 2)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.settingResolution.isVisible = ConfigHelper.isOV
    }

    override fun initData() {
        binding.settingImageAnimation.bindBoolean(ConfigHelper::isImageAnimation)
        binding.settingImageCompress.bindBoolean(ConfigHelper::isImageCompress)
        binding.settingGridAnimation.bindBoolean(ConfigHelper::isAdapterAnimation)
        binding.settingFilterDelete.bindBoolean(ConfigHelper::isFilterDeleteComment)
        binding.settingBreakUp.bindBoolean(ConfigHelper::isFilterBreakUpComment)
        binding.settingEpSplit.bindBoolean(ConfigHelper::isSplitEpList)
        binding.settingSmoothFont.bindBoolean(ConfigHelper::isSmoothFont)
        binding.settingTopicTag.bindBoolean(ConfigHelper::isTopicTimeFlag)
        binding.settingRakuenTab.bindBoolean(ConfigHelper::isRememberRakuenTab)
        binding.settingTimelineTab.bindBoolean(ConfigHelper::isRememberTimelineTab)
        binding.settingForceBrowser.bindBoolean(ConfigHelper::isForceBrowser)
        binding.settingResolution.bindBoolean(ConfigHelper::isAdaptScreen)

        binding.settingDynamicTheme.bindSerializable(
            activity = this,
            property = ConfigHelper::appTheme,
            names = themeNames,
            values = themeValues,
            confirm = { ThemeHelper.instance.recreateAllActivity() }
        )

        binding.settingCommentSort.bindSerializable(
            activity = this,
            property = ConfigHelper::commentDefaultSort,
            names = listOf("时间", "最新", "最热"),
            values = listOf("asc", "desc", "hot")
        )

        binding.settingVpSlop.bindSerializable(
            activity = this,
            property = ConfigHelper::vpTouchSlop,
            names = listOf("2 倍", "3 倍", "4 倍", "5 倍", "6 倍", "7 倍", "8 倍"),
            values = listOf(2, 3, 4, 5, 6, 7, 8)
        )
    }

    override fun initListener() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
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