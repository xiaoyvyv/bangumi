package com.xiaoyv.bangumi.ui.feature.setting.ui

import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.xiaoyv.bangumi.databinding.ActivitySettingUiBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

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
        binding.settingFilterDelete.bindBoolean(ConfigHelper::isFilterDeleteComment)
        binding.settingBreakUp.bindBoolean(ConfigHelper::isFilterBreakUpComment)
        binding.settingEpSplit.bindBoolean(ConfigHelper::isSplitEpList)
        binding.settingSmoothFont.bindBoolean(ConfigHelper::isSmoothFont)
        binding.settingTopicTag.bindBoolean(ConfigHelper::isTopicTimeFlag)
//        binding.settingDynamicTheme.bindBoolean(ConfigHelper::isDynamicTheme)
        binding.settingDynamicTheme.setOnFastLimitClickListener {
            // Kotlin Code
            ColorPickerDialog
                .Builder(this)                        // Pass Activity Instance
                .setTitle("Pick Theme")            // Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(Color.RED)     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                }
                .show()
        }

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