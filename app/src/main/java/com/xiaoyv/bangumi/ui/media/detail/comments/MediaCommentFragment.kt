package com.xiaoyv.bangumi.ui.media.detail.comments

import com.xiaoyv.bangumi.databinding.FragmentCharacterBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaCommentFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCommentFragment : BaseViewModelFragment<FragmentCharacterBinding, MediaCommentViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(mediaId: String): MediaCommentFragment {
            return MediaCommentFragment()
        }
    }
}