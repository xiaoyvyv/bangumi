package com.xiaoyv.bangumi.ui.feature.search

import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.blankj.utilcode.util.KeyboardUtils
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
    private val tagItemAdapter by lazy { SearchAdapter(false) }
    private val otherItemAdapter by lazy { SearchAdapter(false) }
    private val recentlyItemAdapter by lazy { SearchAdapter(true) }

    private val viewPool by lazy { RecycledViewPool() }

    override fun initView() {
        binding.rvSubject.setRecycledViewPool(viewPool)
        binding.rvPerson.setRecycledViewPool(viewPool)
        binding.rvTag.setRecycledViewPool(viewPool)
        binding.rvOther.setRecycledViewPool(viewPool)
        binding.rvRecently.setRecycledViewPool(viewPool)
    }

    override fun initData() {
        binding.rvSubject.adapter = subjectItemAdapter
        binding.rvPerson.adapter = personItemAdapter
        binding.rvTag.adapter = tagItemAdapter
        binding.rvOther.adapter = otherItemAdapter
        binding.rvRecently.adapter = recentlyItemAdapter
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
            KeyboardUtils.showSoftInput(binding.searchBar.etKeyword)
            viewModel.switchSearchType(subjectItemAdapter.getItem(position))
        }

        personItemAdapter.addOnItemChildClickListener(R.id.item_search) { _, _, position ->
            KeyboardUtils.showSoftInput(binding.searchBar.etKeyword)
            viewModel.switchSearchType(personItemAdapter.getItem(position))
        }

        tagItemAdapter.addOnItemChildClickListener(R.id.item_search) { _, _, position ->
            KeyboardUtils.showSoftInput(binding.searchBar.etKeyword)
            viewModel.switchSearchType(tagItemAdapter.getItem(position))
        }

        otherItemAdapter.addOnItemChildClickListener(R.id.item_search) { _, _, position ->
            KeyboardUtils.showSoftInput(binding.searchBar.etKeyword)
            viewModel.switchSearchType(otherItemAdapter.getItem(position))
        }

        recentlyItemAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            KeyboardUtils.hideSoftInput(this)
            RouteHelper.jumpSearchDetail(it)
            finish()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onSearchSubjectLiveData.observe(this) {
            subjectItemAdapter.submitList(it)
        }

        viewModel.onSearchPersonLiveData.observe(this) {
            personItemAdapter.submitList(it)
        }

        viewModel.onSearchTagLiveData.observe(this) {
            tagItemAdapter.submitList(it)
        }

        viewModel.onSearchOtherLiveData.observe(this) {
            otherItemAdapter.submitList(it)
        }

        viewModel.onSearchRecentlyLiveData.observe(this) {
            binding.rvRecently.isVisible = it.isNullOrEmpty().not()
            binding.tvTitleRecently.isVisible = it.isNullOrEmpty().not()
            binding.dividerRecently.isVisible = it.isNullOrEmpty().not()
            recentlyItemAdapter.submitList(it)
        }

        viewModel.currentSearchItem.observe(this) {
            val item = it ?: return@observe
            val hint = buildString {
                append("搜索：")
                append(BgmPathType.string(item.pathType))
                append(" - ")
                append(item.label)
            }
            binding.searchBar.etKeyword.hint = hint

            // 有输入内容切换时，提示搜索内容
            if (binding.searchBar.etKeyword.text.toString().trim().isNotBlank()) {
                showToast(hint)
            }
        }
    }
}