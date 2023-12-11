package com.xiaoyv.bangumi.ui.feature.magi.rank.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isGone
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentMagiRankBinding
import com.xiaoyv.bangumi.ui.feature.magi.rank.MagiRankAdapter
import com.xiaoyv.common.api.parser.entity.MagiRankEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import kotlin.math.max

/**
 * Class: [MagiRankItemBinder]
 *
 * @author why
 * @since 12/12/23
 */
class MagiRankItemBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<MagiRankAdapter.Item, BaseQuickBindingHolder<FragmentMagiRankBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentMagiRankBinding>,
        position: Int,
        item: MagiRankAdapter.Item?
    ) {
        val entity = item?.entity
        if (entity !is MagiRankEntity.MagiRank) return

        val pb1Tip = if (entity.challenge) "已成功" else "已创建"
        holder.binding.pb1.max = max(entity.correctMaxCount, entity.answeredMaxCount)
        holder.binding.pb1.progress = entity.correct
        holder.binding.tv1.text = String.format("$pb1Tip：%d", entity.correct)

        holder.binding.tv2.isGone = entity.challenge == false
        holder.binding.pb2.isGone = entity.challenge == false
        holder.binding.pb2.max = max(entity.correctMaxCount, entity.answeredMaxCount)
        holder.binding.pb2.progress = entity.answered
        holder.binding.tv2.text = String.format("已挑战：%d", entity.answered)

        holder.binding.tvTitle.text = entity.userName
        holder.binding.ivAvatar.loadImageAnimate(entity.userAvatar)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentMagiRankBinding> {
        return BaseQuickBindingHolder(
            FragmentMagiRankBinding.inflate(context.inflater, parent, false)
        )
    }
}