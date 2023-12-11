package com.xiaoyv.bangumi.ui.feature.magi.history

import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.ui.feature.magi.question.MagiQuestionDialog
import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [MagiHistoryFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiHistoryFragment : BaseListFragment<MagiQuestionEntity, MagiHistoryViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = true

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_history) {
            viewModel.queryDetail(it)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onQueryMagiDetail.observe(this) {
            useNotNull(it) {
                MagiQuestionDialog.show(childFragmentManager, this)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MagiQuestionEntity, *> {
        return MagiHistoryAdapter()
    }

    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireContext())
    }

    companion object {
        fun newInstance(): MagiHistoryFragment {
            return MagiHistoryFragment()
        }
    }
}