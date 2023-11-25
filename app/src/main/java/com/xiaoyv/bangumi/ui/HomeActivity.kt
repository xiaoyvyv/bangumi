package com.xiaoyv.bangumi.ui

import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityHomeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity

/**
 * Class: [HomeActivity]
 *
 * @author why
 * @since 11/24/23
 */
class HomeActivity : BaseViewModelActivity<ActivityHomeBinding, HomeViewModel>() {

    private val vpAdapter by lazy { HomeAdapter(this) }

    override fun initView() {
        binding.vpView.isUserInputEnabled = false
        binding.vpView.offscreenPageLimit = vpAdapter.itemCount
        binding.vpView.adapter = vpAdapter
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> binding.vpView.setCurrentItem(0, false)
                R.id.bottom_menu_timeline -> binding.vpView.setCurrentItem(1, false)
                R.id.bottom_menu_media -> binding.vpView.setCurrentItem(2, false)
                R.id.bottom_menu_discover -> binding.vpView.setCurrentItem(3, false)
                R.id.bottom_menu_profile -> binding.vpView.setCurrentItem(4, false)
            }
            true
        }
    }
}