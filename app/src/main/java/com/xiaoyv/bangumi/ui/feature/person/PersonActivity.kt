package com.xiaoyv.bangumi.ui.feature.person

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityPersonBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.randomOffset
import com.xiaoyv.common.kts.randomX
import com.xiaoyv.common.kts.randomY

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
    }

    override fun initView() {
        randomX(binding.topLeftTextView, randomOffset)
        randomY(binding.topLeftTextView, randomOffset)

        randomX(binding.middleLeftTextView, randomOffset)
        randomY(binding.middleLeftTextView, randomOffset)

        randomX(binding.bottomLeftTextView, randomOffset)
        randomY(binding.bottomLeftTextView, randomOffset)

        randomX(binding.topRightTextView, randomOffset)
        randomY(binding.topRightTextView, randomOffset)

        randomX(binding.middleRightTextView, randomOffset)
        randomY(binding.middleRightTextView, randomOffset)

        randomX(binding.bottomRightTextView, randomOffset)
        randomY(binding.bottomRightTextView, randomOffset)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        vpAdapter.personId = viewModel.personId

        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}