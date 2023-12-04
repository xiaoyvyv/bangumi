package com.xiaoyv.bangumi.ui.feature.person.opus

import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.FragmentPersionCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * Class: [PersonOpusFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOpusFragment :
    BaseViewModelFragment<FragmentPersionCharacterBinding, PersonOpusViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(personId: String): PersonOpusFragment {
            return PersonOpusFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}