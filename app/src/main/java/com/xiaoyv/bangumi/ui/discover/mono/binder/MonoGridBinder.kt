package com.xiaoyv.bangumi.ui.discover.mono.binder

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewCharacterItemBinding
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [MonoGridBinder]
 *
 * @author why
 * @since 11/30/23
 */
class MonoGridBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewCharacterItemBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCharacterItemBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        val entity = item?.entity as? SampleImageEntity ?: return
        val binding = holder.binding
        binding.ivAvatar.loadImageAnimate(entity.image, cropType = ImageView.ScaleType.FIT_START)
        binding.tvName.text = entity.title
        binding.tvPerson.text = entity.desc
    }


    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ) = BaseQuickBindingHolder(
        FragmentOverviewCharacterItemBinding.inflate(context.inflater, parent, false)
    )
}