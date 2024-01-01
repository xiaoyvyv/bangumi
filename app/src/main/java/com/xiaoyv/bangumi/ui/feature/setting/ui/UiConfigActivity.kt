package com.xiaoyv.bangumi.ui.feature.setting.ui

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingUiBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog

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
        binding.settingImageAnimation.bindBoolean(ConfigHelper::isImageAnimation)
        binding.settingImageCompress.bindBoolean(ConfigHelper::isImageCompress)
        binding.settingGridAnimation.bindBoolean(ConfigHelper::isAdapterAnimation)
        binding.settingDynamicTheme.bindBoolean(ConfigHelper::isDynamicTheme)
        binding.settingFilterDelete.bindBoolean(ConfigHelper::isFilterDeleteComment)
        binding.settingBreakUp.bindBoolean(ConfigHelper::isFilterBreakUpComment)
        binding.settingEpSplit.bindBoolean(ConfigHelper::isSplitEpList)
        binding.settingSmoothFont.bindBoolean(ConfigHelper::isSmoothFont)
        binding.settingTopicTag.bindBoolean(ConfigHelper::isTopicTimeFlag)

        binding.settingCommentSort.bindSerializable(
            activity = this,
            property = ConfigHelper::commentDefaultSort,
            names = listOf("时间", "最新", "最热"),
            values = listOf("asc", "desc", "hot")
        )

        debugLog { "Init Data ${ConfigHelper.isSmoothFont}" }
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