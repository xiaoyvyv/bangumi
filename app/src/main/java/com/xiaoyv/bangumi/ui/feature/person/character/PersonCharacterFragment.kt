package com.xiaoyv.bangumi.ui.feature.person.character

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersonCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonCharacterFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCharacterFragment :
    BaseViewModelFragment<FragmentPersonCharacterBinding, PersonCharacterViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String, isVirtual: Boolean): PersonCharacterFragment {
            return PersonCharacterFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to personId,
                    NavKey.KEY_BOOLEAN to isVirtual
                )
            }
        }
    }
}