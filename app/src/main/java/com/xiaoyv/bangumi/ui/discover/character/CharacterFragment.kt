package com.xiaoyv.bangumi.ui.discover.character

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [CharacterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class CharacterFragment : BaseViewModelFragment<FragmentCharacterBinding, CharacterViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): CharacterFragment {
            return CharacterFragment()
        }
    }
}