package com.xiaoyv.bangumi.ui.discover.wiki

import com.xiaoyv.bangumi.databinding.FragmentWikiBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [WikiFragment]
 *
 * @author why
 * @since 11/24/23
 */
class WikiFragment : BaseViewModelFragment<FragmentWikiBinding, WikiViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): WikiFragment {
            return WikiFragment()
        }
    }
}