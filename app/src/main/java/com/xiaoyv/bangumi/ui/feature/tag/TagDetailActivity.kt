package com.xiaoyv.bangumi.ui.feature.tag

import android.content.Intent
import android.os.Bundle
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.search.detail.adapter.SearchDetailItemAdapter
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [TagDetailActivity]
 *
 * @author why
 * @since 12/10/23
 */
class TagDetailActivity : BaseListActivity<SearchResultEntity, TagDetailViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mediaType = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.tag = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
    }

    override fun initView() {
        super.initView()
        binding.toolbar.title = buildString {
            append(GlobalConfig.mediaTypeName(viewModel.mediaType))
            append("标签：")
            append(viewModel.tag)
        }
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<SearchResultEntity, *> {
        return SearchDetailItemAdapter()
    }
}