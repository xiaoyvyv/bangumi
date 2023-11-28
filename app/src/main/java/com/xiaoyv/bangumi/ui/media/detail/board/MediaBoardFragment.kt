package com.xiaoyv.bangumi.ui.media.detail.board

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaBoardFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaBoardFragment : BaseViewModelFragment<FragmentCharacterBinding, MediaBoardViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): MediaBoardFragment {
            return MediaBoardFragment()
        }
    }
}