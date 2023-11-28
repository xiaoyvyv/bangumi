package com.xiaoyv.bangumi.ui.media.detail.character

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaCharacterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCharacterFragment : BaseViewModelFragment<FragmentCharacterBinding, MediaCharacterViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(mediaId: String): MediaCharacterFragment {
            return MediaCharacterFragment()
        }
    }
}