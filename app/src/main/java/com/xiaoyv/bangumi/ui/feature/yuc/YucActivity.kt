package com.xiaoyv.bangumi.ui.feature.yuc

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityYucBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.config.bean.YucSectionEntity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.setOnItemChildLongClickListener

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
        // 一月新番
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_winter) {
            RouteHelper.jumpYucDetail(it.id + "-1")
        }
        itemAdapter.setOnItemChildLongClickListener(R.id.iv_winter) { v, data ->
            showPopupmenu(v, data, 1..3)
            true
        }

        // 四月新番
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_spring) {
            RouteHelper.jumpYucDetail(it.id + "-4")
        }
        itemAdapter.setOnItemChildLongClickListener(R.id.iv_spring) { v, data ->
            showPopupmenu(v, data, 4..6)
            true
        }

        // 七月新番
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_summer) {
            RouteHelper.jumpYucDetail(it.id + "-7")
        }
        itemAdapter.setOnItemChildLongClickListener(R.id.iv_summer) { v, data ->
            showPopupmenu(v, data, 7..9)
            true
        }

        // 十月新番
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_autumn) {
            RouteHelper.jumpYucDetail(it.id + "-10")
        }
        itemAdapter.setOnItemChildLongClickListener(R.id.iv_autumn) { v, data ->
            showPopupmenu(v, data, 10..12)
            true
        }

    }

    private fun showPopupmenu(view: View, data: YucSectionEntity, intRange: IntRange) {
        PopupMenu(this, view)
            .apply {
                for (i in intRange) {
                    menu.add("${i}月新番")
                        .setOnMenuItemClickListener {
                            RouteHelper.jumpYucDetail(data.id + "-$i")
                            true
                        }
                }
            }
            .show()
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