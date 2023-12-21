package com.xiaoyv.bangumi.ui.feature.user.mono

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivityUserContainerBinding
import com.xiaoyv.bangumi.ui.discover.mono.MonoFragment
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.LazyUtils.loadRootFragment
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [UserMonoActivity]
 *
 * @author why
 * @since 12/19/23
 */
class UserMonoActivity : BaseBindingActivity<ActivityUserContainerBinding>() {
    private var userId: String = ""
    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        userId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.title = "TA 的人物"
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        loadRootFragment(binding.flContainer.id, MonoFragment.newInstance(userId, false))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}