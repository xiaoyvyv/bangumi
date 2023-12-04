package com.xiaoyv.bangumi.ui.feature.person.collect

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersionCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonCollectFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCollectFragment :
    BaseViewModelFragment<FragmentPersionCharacterBinding, PersonCollectViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String): PersonCollectFragment {
            return PersonCollectFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}