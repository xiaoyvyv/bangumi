package com.xiaoyv.bangumi.ui.feature.person

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityPersonBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.helper.FixHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.loadImageBlurBackground
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [PersonActivity]
 *
 * @author why
 * @since 12/4/23
 */
class PersonActivity : BaseViewModelActivity<ActivityPersonBinding, PersonViewModel>() {

    private val vpAdapter by lazy {
        PersonAdapter(supportFragmentManager, this.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.personId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.isVirtual = bundle.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        FixHelper.fixCool(binding.ivBanner, binding.toolbarLayout, 204.dpi)
    }

    override fun initData() {
        vpAdapter.isVirtual = viewModel.isVirtual
        vpAdapter.personId = viewModel.personId

        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initListener() {
        binding.vpContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if ((position == 1 || position == 2) && viewModel.isVirtual.not()) {
                    binding.fabTop.show()
                } else {
                    binding.fabTop.hide()
                }
            }
        })
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onPersonLiveData.observe(this) {
            val entity = it ?: return@observe

            binding.ivCover.loadImageBlurBackground(entity.poster)
            binding.ivCover.loadImageAnimate(entity.poster, cropType = ImageView.ScaleType.FIT_START)
            binding.ivCover.setOnFastLimitClickListener {
                RouteHelper.jumpPreviewImage(entity.posterLarge)
            }

            binding.toolbar.title = entity.nameNative
            binding.tvTitle.text = entity.nameNative
            binding.tvSubtitle.text = entity.nameCn
            binding.tvJob.text = if (entity.isVirtual) "虚拟角色" else entity.job

            binding.ivBanner.loadImageBlur(entity.poster)

            invalidateOptionsMenu()
        }

        viewModel.vpEnableLiveData.observe(this) {
            binding.vpContent.isUserInputEnabled = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val isCollected = viewModel.isCollected
        menu.add(if (isCollected) "取消收藏" else "收藏")
            .setIcon(if (isCollected) CommonDrawable.ic_bookmark_added else CommonDrawable.ic_bookmark_add)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (UserHelper.isLogin.not()) {
                    RouteHelper.jumpLogin()
                    return@setOnMenuItemClickListener true
                }

                val tip = if (isCollected) "是否取消收藏该人物？" else "是否收藏该人物？"
                showConfirmDialog(message = tip) {
                    viewModel.actionCollection(isCollected.not())
                }
                true
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