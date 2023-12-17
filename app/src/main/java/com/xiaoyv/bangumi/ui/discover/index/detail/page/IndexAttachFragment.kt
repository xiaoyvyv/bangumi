package com.xiaoyv.bangumi.ui.discover.index.detail.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.ui.discover.index.detail.IndexDetailViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.IndexAttachEntity
import com.xiaoyv.common.config.annotation.IndexTabCatType
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [IndexAttachFragment]
 *
 * @author why
 * @since 12/17/23
 */
class IndexAttachFragment : BaseListFragment<IndexAttachEntity, IndexAttachViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    private val activityViewModel by activityViewModels<IndexDetailViewModel>()

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.indexId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.indexTabCatType = arguments.getString(NavKey.KEY_STRING_SECOND).orEmpty()
    }

    override fun initView() {
        viewModel.activityViewModel = activityViewModel
        super.initView()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<IndexAttachEntity, *> {
        return IndexAttachAdapter()
    }

    companion object {
        fun newInstance(
            indexId: String,
            @IndexTabCatType indexTabCatType: String,
        ): IndexAttachFragment {
            return IndexAttachFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to indexId,
                    NavKey.KEY_STRING_SECOND to indexTabCatType
                )
            }
        }
    }
}