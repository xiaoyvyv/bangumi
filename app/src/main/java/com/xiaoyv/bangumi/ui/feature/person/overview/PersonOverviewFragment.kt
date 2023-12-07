package com.xiaoyv.bangumi.ui.feature.person.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.person.PersonViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.annotation.SampleImageGridClickType
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager

/**
 * Class: [PersonOverviewFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOverviewFragment :
    BaseViewModelFragment<FragmentPersonOverviewBinding, PersonOverviewViewModel>() {

    private val personViewModel: PersonViewModel by activityViewModels()

    private val itemAdapter by lazy {
        PersonOverviewAdapter(
            touchedListener = RecyclerItemTouchedListener {
                personViewModel.vpEnableLiveData.value = it
            },
            clickSubItem = { type, id ->
                when (type) {
                    SampleImageGridClickType.TYPE_USER -> RouteHelper.jumpUserDetail(id)
                    SampleImageGridClickType.TYPE_PERSON_REAL -> RouteHelper.jumpPerson(id, false)
                    SampleImageGridClickType.TYPE_PERSON_VIRTUAL -> RouteHelper.jumpPerson(id, true)
                    SampleImageGridClickType.TYPE_INDEX -> RouteHelper.jumpIndexDetail(id)
                    SampleImageGridClickType.TYPE_OPUS -> RouteHelper.jumpMediaDetail(id)
                }
            }
        )
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.personId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.isVirtual = arguments.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {

    }

    override fun initData() {
        binding.rvContent.layoutManager =
            AnimeLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                .apply {
                    extraLayoutSpaceScale = 2
                }

        binding.rvContent.adapter = itemAdapter
    }

    override fun initListener() {
        itemAdapter.setOnDebouncedChildClickListener(R.id.item_grid) {
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = personViewModel.loadingViewState,
            loadingBias = 0.2f
        )

        personViewModel.onPersonLiveData.observe(this) {
            val entity = it ?: return@observe

            launchUI {
                itemAdapter.submitList(viewModel.buildBinderList(entity))
            }
        }
    }

    companion object {
        fun newInstance(personId: String, isVirtual: Boolean): PersonOverviewFragment {
            return PersonOverviewFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to personId,
                    NavKey.KEY_BOOLEAN to isVirtual
                )
            }
        }
    }
}