package com.xiaoyv.bangumi.ui.discover.blog

import com.xiaoyv.bangumi.databinding.FragmentBlogBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [BlogFragment]
 *
 * @author why
 * @since 11/24/23
 */
class BlogFragment : BaseViewModelFragment<FragmentBlogBinding, BlogViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): BlogFragment {
            return BlogFragment()
        }
    }
}