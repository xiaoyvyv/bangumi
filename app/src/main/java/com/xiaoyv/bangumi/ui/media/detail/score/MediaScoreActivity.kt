package com.xiaoyv.bangumi.ui.media.detail.score

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityMediaScoreBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.adjustScrollSensitivity

/**
 * Class: [MediaScoreActivity]
 *
 * @author why
 * @since 1/22/24
 */
class MediaScoreActivity : BaseViewModelActivity<ActivityMediaScoreBinding, MediaScoreViewModel>() {

    private val vpAdapter by lazy {
        MediaScoreAdapter(supportFragmentManager, lifecycle, viewModel.mediaType)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mediaId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.mediaType = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        viewModel.forFriend = bundle.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
        binding.toolbar.title = if (viewModel.forFriend) "好友评分" else "评分详情"

        vpAdapter.mediaId = viewModel.mediaId
        vpAdapter.friend = viewModel.forFriend

        binding.vp2.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount
        binding.vp2.adapter = vpAdapter
        binding.vp2.setCurrentItem(1, false)

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.addCommonMenu(BgmApiManager.buildReferer(BgmPathType.TYPE_SCORE, viewModel.mediaId))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}