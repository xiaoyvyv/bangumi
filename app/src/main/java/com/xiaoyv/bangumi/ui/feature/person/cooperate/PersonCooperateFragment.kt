package com.xiaoyv.bangumi.ui.feature.person.cooperate

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersionCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonCooperateFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCooperateFragment :
    BaseViewModelFragment<FragmentPersionCharacterBinding, PersonCooperateViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String): PersonCooperateFragment {
            return PersonCooperateFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}