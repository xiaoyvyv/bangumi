package com.xiaoyv.bangumi.ui.discover.container

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivityEmptyContainerBinding
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.bangumi.ui.process.ProcessFragment
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.LazyUtils.loadRootFragment
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [FragmentContainerActivity]
 *
 * @author why
 * @since 12/24/23
 */
class FragmentContainerActivity : BaseBindingActivity<ActivityEmptyContainerBinding>() {
    private var page = ""

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        page = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {

    }

    override fun initData() {
        when (page) {
            FeatureType.TYPE_RANK -> {
                loadRootFragment(binding.flContainer.id, MediaFragment.newInstance())
            }

            FeatureType.TYPE_PROCESS -> {
                loadRootFragment(binding.flContainer.id, ProcessFragment.newInstance())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}