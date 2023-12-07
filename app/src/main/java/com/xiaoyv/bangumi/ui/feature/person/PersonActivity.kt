package com.xiaoyv.bangumi.ui.feature.person

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityPersonBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.loadImageBlurBackground

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
            debugLog { "Person: " + entity.toJson(true) }

            binding.ivBanner.loadImageBlur(entity.poster)
            binding.ivCover.loadImageBlurBackground(entity.poster)
            binding.ivCover.loadImageAnimate(entity.poster, centerCrop = false)
            binding.toolbar.title = entity.nameNative

            binding.tvTitle.text = entity.nameNative
            binding.tvSubtitle.text = entity.nameCn
            binding.tvJob.text = if (entity.isVirtual) "虚拟角色" else entity.job
        }

        viewModel.vpEnableLiveData.observe(this) {
            binding.vpContent.isUserInputEnabled = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}