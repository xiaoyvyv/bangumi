package com.xiaoyv.bangumi.ui.media.detail.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentOverviewBinding
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [OverviewFragment]
 *
 * @author why
 * @since 11/24/23
 */
class OverviewFragment : BaseViewModelFragment<FragmentOverviewBinding, OverviewViewModel>() {

    private val mediaViewModel by activityViewModels<MediaDetailViewModel>()

    private val overviewAdapter by lazy {
        OverviewAdapter(RecyclerItemTouchedListener {
            mediaViewModel.vpEnableLiveData.value = it
        })
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.rvContent.adapter = overviewAdapter

        binding.srlRefresh.initRefresh()
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        viewModel.queryMediaInfo()
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.mediaDetailLiveData.observe(this) {
            mediaViewModel.onMediaDetailLiveData.value = it
        }

        viewModel.mediaBinderListLiveData.observe(this) {
            overviewAdapter.submitList(it)
        }
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryMediaInfo()
        }
    }

    companion object {
        fun newInstance(mediaId: String): OverviewFragment {
            return OverviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}