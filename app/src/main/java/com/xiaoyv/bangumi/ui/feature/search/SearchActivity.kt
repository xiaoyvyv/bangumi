package com.xiaoyv.bangumi.ui.feature.search

import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivitySearchBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [SearchActivity]
 *
 * @author why
 * @since 12/9/23
 */
class SearchActivity : BaseViewModelActivity<ActivitySearchBinding, SearchViewModel>() {

    private val subjectItemAdapter by lazy { SearchAdapter(false) }
    private val personItemAdapter by lazy { SearchAdapter(false) }
    private val recentlyItemAdapter by lazy { SearchAdapter(true) }

    override fun initView() {
        binding.rvSubject.adapter = subjectItemAdapter
        binding.rvPerson.adapter = personItemAdapter
        binding.rvRecently.adapter = recentlyItemAdapter
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.searchBar.tvCancel.setOnFastLimitClickListener {
            finish()
        }

        binding.searchBar.etKeyword.setOnEditorActionListener { _, _, _ ->
            val item =
                viewModel.currentSearchItem.value ?: return@setOnEditorActionListener true
            val keyword = binding.searchBar.etKeyword.text.toString().trim()
            if (keyword.isNotBlank()) {
                item.keyword = keyword
                RouteHelper.jumpSearchDetail(item)
                finish()
            }
            true
        }

        subjectItemAdapter.addOnItemChildClickListener(R.id.item_search) { _, _, position ->
            viewModel.currentSearchItem.value = subjectItemAdapter.getItem(position)
        }

        personItemAdapter.addOnItemChildClickListener(R.id.item_search) { _, _, position ->
            viewModel.currentSearchItem.value = personItemAdapter.getItem(position)
        }

        recentlyItemAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            RouteHelper.jumpSearchDetail(it)
            finish()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onSearchSubjectLiveData.observe(this) {
            subjectItemAdapter.submitList(it)
        }

        viewModel.onSearchPersonLiveData.observe(this) {
            binding.rvRecently.isVisible = it.isNotEmpty()
            binding.tvTitleRecently.isVisible = it.isNotEmpty()
            binding.dividerRecently.isVisible = it.isNotEmpty()
            personItemAdapter.submitList(it)
        }

        viewModel.onSearchRecentlyLiveData.observe(this) {
            recentlyItemAdapter.submitList(it)
        }

        viewModel.currentSearchItem.observe(this) {
            binding.searchBar.etKeyword.hint = buildString {
                append("搜索：")
                append(BgmPathType.string(it.pathType))
                append(" - ")
                append(it.label)
            }
        }
    }
}