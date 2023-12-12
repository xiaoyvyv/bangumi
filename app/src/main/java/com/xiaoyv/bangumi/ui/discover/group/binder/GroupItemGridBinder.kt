package com.xiaoyv.bangumi.ui.discover.group.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentGroupItemBinding
import com.xiaoyv.bangumi.ui.discover.group.GroupAdapter
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailAdapter
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

class GroupItemGridBinder(
    private val viewPool: RecyclerView.RecycledViewPool,
    private val onClickGroupListener: (SampleAvatar) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<GroupAdapter.Item, BaseQuickBindingHolder<FragmentGroupItemBinding>> {
    private val groupAdapter by lazy {
        GroupDetailAdapter().apply {
            setOnDebouncedChildClickListener(R.id.iv_avatar, block = onClickGroupListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentGroupItemBinding>,
        position: Int,
        item: GroupAdapter.Item?
    ) {
        item ?: return
        holder.binding.sectionHot.title = item.title
        groupAdapter.submitList(item.entity.forceCast())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentGroupItemBinding> {
        val binding = FragmentGroupItemBinding.inflate(context.inflater, parent, false)
        binding.rvOther.setRecycledViewPool(viewPool)
        binding.rvOther.adapter = groupAdapter
        return BaseQuickBindingHolder(binding)
    }
}