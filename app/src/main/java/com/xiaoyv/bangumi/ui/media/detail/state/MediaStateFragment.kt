package com.xiaoyv.bangumi.ui.media.detail.state

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaStateFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaStateFragment : BaseViewModelFragment<FragmentCharacterBinding, MediaStateViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(mediaId: String): MediaStateFragment {
            return MediaStateFragment()
        }
    }
}