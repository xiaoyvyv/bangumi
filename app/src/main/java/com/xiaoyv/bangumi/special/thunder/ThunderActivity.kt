package com.xiaoyv.bangumi.special.thunder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityThunderBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.clear
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [ThunderActivity]
 *
 * @author why
 * @since 3/23/24
 */
class ThunderActivity : BaseViewModelActivity<ActivityThunderBinding, ThunderViewModel>() {

    private val vpAdapter by lazy { ThunderAdapter(this) }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        if (ConfigHelper.isOsSupportArm) when {
            // 种子文件
            intent.type == "application/x-bittorrent" -> {
                viewModel.onTorrentUriLiveData.value = intent.data
            }
            // 链接
            viewModel.supportScheme.contains(intent.scheme) || bundle.containsKey(NavKey.KEY_PARCELABLE) -> {
                viewModel.onIntentUriLiveData.value = intent.data
            }
        }
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount
        binding.vpContent.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpContent.adapter = vpAdapter
        tabLayoutMediator.attach()
    }

    override fun initListener() {

    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState
        )

        // 种子文件
        viewModel.onTorrentUriLiveData.observe(this) {
            val uri = it ?: return@observe
            viewModel.handleTorrentUri(uri)
            viewModel.onTorrentUriLiveData.clear()
        }

        // 链接
        viewModel.onIntentUriLiveData.observe(this) {
            val uri = it ?: return@observe
            viewModel.handleLinkUri(uri)
            viewModel.onIntentUriLiveData.clear()
        }

        // 解析到的种子信息
        viewModel.onTorrentInfoLiveData.observe(this) {
            val torrentInfo = it ?: return@observe
            RouteHelper.jumpTorrentInfo(torrentInfo)
            viewModel.onTorrentInfoLiveData.clear()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(i18n(CommonString.download_add))
            .setIcon(CommonDrawable.ic_add)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (ConfigHelper.isOsSupportArm) {
                    ThunderDialog.show(this) {
                        viewModel.handleLinkUri(it)
                    }
                } else {
                    showToastCompat(i18n(CommonString.download_not_arm))
                }
                true
            }

        menu.add(i18n(CommonString.common_help))
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.download_dialog_input_hint),
                    cancelText = null
                )
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this) {
            if (ActivityUtils.getActivityList().size == 1) {
                RouteHelper.jumpHome()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}