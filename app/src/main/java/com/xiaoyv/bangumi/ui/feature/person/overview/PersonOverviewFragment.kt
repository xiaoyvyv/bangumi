package com.xiaoyv.bangumi.ui.feature.person.overview

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentPersonCharacterBinding
import com.xiaoyv.bangumi.ui.feature.person.PersonViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonOverviewFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOverviewFragment :
    BaseViewModelFragment<FragmentPersonCharacterBinding, PersonOverviewViewModel>() {

    private val personViewModel: PersonViewModel by activityViewModels()

    override fun initView() {

    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = personViewModel.loadingViewState,
            loadingBias = 0.2f
        )
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