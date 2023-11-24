package com.xiaoyv.bangumi.ui.media

import com.xiaoyv.bangumi.databinding.FragmentMediaBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MediaFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaFragment : BaseViewModelFragment<FragmentMediaBinding, MediaViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): MediaFragment {
            return MediaFragment()
        }
    }
}