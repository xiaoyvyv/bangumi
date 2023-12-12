package com.xiaoyv.bangumi.ui.discover.index.detail

import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivityIndexDetailBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [IndexDetailActivity]
 *
 * @author why
 * @since 12/12/23
 */
class IndexDetailActivity :
    BaseViewModelActivity<ActivityIndexDetailBinding, IndexDetailViewModel>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}