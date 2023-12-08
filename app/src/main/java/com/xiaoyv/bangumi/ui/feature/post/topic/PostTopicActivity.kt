package com.xiaoyv.bangumi.ui.feature.post.topic

import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivityPostTopicBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [PostTopicActivity]
 *
 * @author why
 * @since 12/8/23
 */
class PostTopicActivity : BaseViewModelActivity<ActivityPostTopicBinding, PostTopicViewModel>() {

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