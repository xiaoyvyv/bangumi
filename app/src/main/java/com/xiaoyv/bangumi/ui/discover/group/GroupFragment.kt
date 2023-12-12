package com.xiaoyv.bangumi.ui.discover.group

import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentGroupBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [GroupFragment]
 *
 * @author why
 * @since 11/24/23
 */
class GroupFragment : BaseViewModelFragment<FragmentGroupBinding, GroupViewModel>() {
    private val contentAdapter by lazy {
        GroupAdapter(
            onClickGroupListener = {
                RouteHelper.jumpGroupDetail(it.id)
            },
            onClickTopicListener = {
                RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_GROUP)
            }
        )
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(requireContext().getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter

        viewModel.queryGroupIndex()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryGroupIndex()
        }

        contentAdapter.setOnDebouncedChildClickListener(com.xiaoyv.common.R.id.tv_more) {
            RouteHelper.jumpGroupList()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { !binding.srlRefresh.isRefreshing },
            showContentDelay = 100
        )

        viewModel.onGroupIndexLiveData.observe(this) {
            val entity = it ?: return@observe
            contentAdapter.submitList(entity)
        }
    }

    companion object {
        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }
}