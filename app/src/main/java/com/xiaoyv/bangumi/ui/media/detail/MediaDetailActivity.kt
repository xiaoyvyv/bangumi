package com.xiaoyv.bangumi.ui.media.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityMediaDetailBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [MediaDetailActivity]
 *
 * @author why
 * @since 11/24/23
 */
class MediaDetailActivity :
    BaseViewModelActivity<ActivityMediaDetailBinding, MediaDetailViewModel>() {

    private val vpAdapter by lazy {
        MediaDetailAdapter(supportFragmentManager, lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mediaId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.mediaType = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        viewModel.mediaName = bundle.getString(NavKey.KEY_STRING_THIRD).orEmpty()
    }

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)
        binding.toolbar.title = viewModel.mediaName
    }


    override fun initData() {
        vpAdapter.mediaId = viewModel.mediaId

        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = 5

        tabLayoutMediator.attach()
    }

    override fun initListener() {

    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun LifecycleOwner.initViewObserver() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}