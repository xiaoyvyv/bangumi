package com.xiaoyv.bangumi.ui.feature.person.character

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersionCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonCharacterFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCharacterFragment :
    BaseViewModelFragment<FragmentPersionCharacterBinding, PersonCharacterViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String): PersonCharacterFragment {
            return PersonCharacterFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}