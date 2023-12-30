package com.xiaoyv.bangumi.ui.discover.index.detail.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.index.detail.IndexDetailViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.IndexAttachEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.IndexTabCatType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

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

        binding.rvContent.updatePadding(top = 8.dpi, bottom = 8.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_index) {
            when (it.pathType) {
                BgmPathType.TYPE_SUBJECT -> RouteHelper.jumpMediaDetail(it.id)
                BgmPathType.TYPE_PERSON -> RouteHelper.jumpPerson(it.id, false)
                BgmPathType.TYPE_CHARACTER -> RouteHelper.jumpPerson(it.id, true)
                BgmPathType.TYPE_EP -> RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
            }
        }
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