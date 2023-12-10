package com.xiaoyv.bangumi.ui.feature.summary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivitySummaryBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [SummaryActivity]
 *
 * @author why
 * @since 12/10/23
 */
class SummaryActivity : BaseViewModelActivity<ActivitySummaryBinding, SummaryViewModel>() {
    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.summary.value = bundle.getStringArray(NavKey.KEY_SERIALIZABLE_ARRAY).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.summary.observe(this) {
            binding.tvSummary.text = null
            it.forEach { text ->
                binding.tvSummary.append(text.parseHtml())
                binding.tvSummary.append("\n")
            }

            invalidateOptionsMenu()
        }

        viewModel.summaryTranslate.observe(this) {
            binding.tvSummary.text = it

            invalidateOptionsMenu()
        }

        // 配置翻译弹窗
        viewModel.onNeedConfig.observe(this) {
            showConfirmDialog(
                message = "你需要先花半分钟配置《百度翻译》的 AppId 和 Secret。\n\n没有的话可以百度开放平台注册，个人每月5万字免费额度",
                neutralText = "去申请",
                confirmText = "去配置",
                onConfirmClick = {
                    RouteHelper.jumpSetting()
                },
                onNeutralClick = {
                    openInBrowser("http://api.fanyi.baidu.com/api/trans/product/desktop")
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isShowOriginal) {
            menu.add("翻译")
                .setIcon(CommonDrawable.ic_translate)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    viewModel.shoTranslate()
                    true
                }
        } else {
            menu.add("原文")
                .setIcon(CommonDrawable.ic_original)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    viewModel.showOriginal()
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}