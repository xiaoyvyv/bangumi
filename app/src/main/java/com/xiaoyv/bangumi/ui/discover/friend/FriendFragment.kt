package com.xiaoyv.bangumi.ui.discover.friend

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [FriendFragment]
 *
 * @author why
 * @since 11/24/23
 */
class FriendFragment : BaseViewModelFragment<FragmentCharacterBinding, FriendViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): FriendFragment {
            return FriendFragment()
        }
    }
}