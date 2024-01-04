package com.xiaoyv.bangumi.ui.feature.magi

import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityMagiBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.widget.kts.adjustScrollSensitivity


/**
 * Class: [MagiActivity]
 *
 * @author why
 * @since 11/24/23
 */
class MagiActivity : BaseViewModelActivity<ActivityMagiBinding, MagiViewModel>() {

    private val vpAdapter by lazy {
        MagiAdapter(supportFragmentManager, lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount
        binding.vp2.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vp2.adapter = vpAdapter

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): MagiActivity {
            return MagiActivity()
        }
    }
}