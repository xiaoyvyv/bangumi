package com.xiaoyv.bangumi.ui.feature.person.overview

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersionCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonOverviewFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOverviewFragment :
    BaseViewModelFragment<FragmentPersionCharacterBinding, PersonOverviewViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String): PersonOverviewFragment {
            return PersonOverviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}