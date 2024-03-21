package com.xiaoyv.bangumi.ui.feature.search.detail

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.KeyboardUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivitySearchDetailBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [SearchDetailActivity]
 *
 * @author why
 * @since 12/10/23
 */
class SearchDetailActivity :
    BaseViewModelActivity<ActivitySearchDetailBinding, SearchDetailViewModel>() {

    private val vpAdapter by lazy {
        SearchDetailAdapter(supportFragmentManager, lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.currentSearchItem.value = bundle.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.vpContent.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount
        binding.vpContent.adapter = vpAdapter

        tabLayoutMediator.attach()

        // 初始化的搜索 TAB
        binding.vpContent.setCurrentItem(viewModel.targetSearchTab, false)
    }

    override fun initData() {
        val keyword = viewModel.currentSearchItem.value?.keyword.orEmpty()
        viewModel.onKeyword.value = keyword
        binding.searchBar.etKeyword.setText(keyword)
    }

    override fun initListener() {
        binding.searchBar.tvCancel.setOnFastLimitClickListener {
            finish()
        }

        binding.searchBar.etKeyword.setOnEditorActionListener { _, _, _ ->
            // 搜索
            val keyword = binding.searchBar.etKeyword.text.toString().trim()
            if (keyword.isNotBlank()) {
                viewModel.onKeyword.value = keyword
                KeyboardUtils.hideSoftInput(this)
            }
            true
        }

        binding.searchBar.etKeyword.doAfterTextChanged {
            viewModel.onKeywordChange.value = it.toString().trim()
        }
    }
}