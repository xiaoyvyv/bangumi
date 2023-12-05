package com.xiaoyv.bangumi.ui.media.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityMediaDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
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
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initListener() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onMediaDetailLiveData.observe(this) {
            if (it == null) {
                return@observe
            }

            binding.ivCover.loadImageAnimate(it.cover)
            binding.ivBanner.loadImageBlur(it.cover)

            binding.toolbar.title = it.titleCn.ifBlank { it.titleNative }
            binding.tvTitle.text = it.titleCn.ifBlank { it.titleNative }
            binding.tvSubtitle.text = it.titleNative
            binding.tvScore.text = String.format("%.1f", it.rating.globalRating)
            binding.tvScoreTip.text = it.rating.description

            if (it.subtype.isNotBlank()) {
                binding.tvTime.text = String.format("(%s - %s)", it.time, it.subtype)
            } else {
                binding.tvTime.text = String.format("(%s)", it.time)
            }
        }

        viewModel.vpEnableLiveData.observe(this) {
            binding.vpContent.isUserInputEnabled = it
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Review")
            .setIcon(CommonDrawable.ic_review)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (UserHelper.isLogin) {
                    RouteHelper.jumpPostBlog(viewModel.onMediaDetailLiveData.value)
                } else {
                    RouteHelper.jumpLogin()
                }
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}