package com.xiaoyv.bangumi.special.yuc

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityYucBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener

/**
 * Class: [YucActivity]
 *
 * @author why
 * @since 3/19/24
 */
class YucActivity : BaseViewModelActivity<ActivityYucBinding, YucViewModel>() {
    private val itemAdapter by lazy { YucAdapter() }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.rvItems.adapter = itemAdapter
    }

    override fun initListener() {
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_winter) {
            RouteHelper.jumpYucDetail(it.id + "-1")
        }

        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_spring) {
            RouteHelper.jumpYucDetail(it.id + "-4")
        }

        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_summer) {
            RouteHelper.jumpYucDetail(it.id + "-7")
        }

        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_autumn) {
            RouteHelper.jumpYucDetail(it.id + "-10")
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onYucSectionLiveData.observe(this) {
            itemAdapter.submitList(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}