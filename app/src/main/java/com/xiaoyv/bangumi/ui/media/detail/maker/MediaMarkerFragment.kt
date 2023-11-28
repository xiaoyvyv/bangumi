package com.xiaoyv.bangumi.ui.media.detail.maker

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaMarkerFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaMarkerFragment : BaseViewModelFragment<FragmentCharacterBinding, MediaMakerViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(mediaId: String): MediaMarkerFragment {
            return MediaMarkerFragment()
        }
    }
}