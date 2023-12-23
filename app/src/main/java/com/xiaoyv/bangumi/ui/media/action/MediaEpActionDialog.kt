/*
package com.xiaoyv.bangumi.ui.media.action

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaActionEpBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpItemBinding
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.EpSaveProgress
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

*/
/**
 * Class: [MediaEpActionDialog]
 *
 * @author why
 * @since 12/18/23
 *//*

class MediaEpActionDialog : DialogFragment() {
    private var onSaveListener: ((Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionEpBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mediaProgress = arguments?.getParcelObj<EpSaveProgress>(NavKey.KEY_PARCELABLE)
        if (mediaProgress != null) {
            val binding = FragmentMediaActionEpBinding.bind(view)
            initView(binding, mediaProgress)
        }
    }

    private fun initView(binding: FragmentMediaActionEpBinding, mediaProgress: EpSaveProgress) {
        binding.tvTitle.text = mediaProgress.mediaName
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        // 格子填充
        val epAdapter = ItemEpAdapter(mediaProgress.myProgress - 1)
        binding.rvEp.updateLayoutParams<ConstraintLayout.LayoutParams> {
            matchConstraintMaxHeight = (ScreenUtils.getAppScreenHeight() * 0.7f).roundToInt()
        }
        binding.rvEp.hasFixedSize()
        binding.rvEp.adapter = epAdapter

        // 点击更新颜色
        epAdapter.setOnDebouncedChildClickListener(R.id.item_ep) {
            epAdapter.selectIndex = epAdapter.itemIndexOfFirst(it)
            if (epAdapter.selectIndex >= 0 && epAdapter.selectIndex < epAdapter.itemCount) {
                epAdapter.notifyItemRangeChanged(
                    0,
                    epAdapter.itemCount,
                    PAYLOAD_REFRESH_COLOR
                )

                finishEp(mediaProgress.mediaId, it.number)
            }
        }

        // 加载数据
        launchUI {
            epAdapter.submitList(withContext(Dispatchers.IO) {
                val numberList = arrayListOf<EpNoEntity>()
                repeat(mediaProgress.totalProgress.coerceAtLeast(0)) {
                    numberList.add(EpNoEntity(it.toString(), (it + 1).toString()))
                }
                numberList
            })
        }
    }

    */
/**
     * 保存进度
     *//*

    private fun finishEp(mediaId: String, number: String) {
        launchUI(
            error = {
                it.printStackTrace()
                isCancelable = true
                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                isCancelable = false

                require(mediaId.isNotBlank()) { "条目信息丢失" }
                require(number.isNotBlank()) { "章节信息丢失" }

                showSnackBar("正在保存进度...", SnackbarUtils.LENGTH_INDEFINITE)

                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.updateMediaProgress(mediaId, watch = number)
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)
                hideSnackBar()

                onSaveListener?.invoke(number.toIntOrNull() ?: 0)

                dismissAllowingStateLoss()
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(ConfigHelper.DIALOG_DIM_AMOUNT)
        window.updateWindowParams {
            width = ScreenUtils.getScreenWidth() - 32.dpi
            gravity = Gravity.CENTER
        }
    }

    data class EpNoEntity(override var id: String, var number: String) : IdEntity

    internal class ItemEpAdapter(var selectIndex: Int = -1) :
        BaseQuickDiffBindingAdapter<EpNoEntity, FragmentOverviewEpItemBinding>(IdDiffItemCallback()) {

        override fun onBindViewHolder(
            holder: BaseQuickBindingHolder<FragmentOverviewEpItemBinding>,
            position: Int,
            item: EpNoEntity?,
            payloads: List<Any>,
        ) {
            super.onBindViewHolder(holder, position, item, payloads)
            payloads.forEach {
                if (it == PAYLOAD_REFRESH_COLOR) {
                    refreshColor(position, holder)
                }
            }
        }

        override fun onBindViewHolder(
            holder: BaseQuickBindingHolder<FragmentOverviewEpItemBinding>,
            position: Int,
            item: EpNoEntity?,
        ) {
            super.onBindViewHolder(holder, position, item)

            // 完成进度复用时的UI逻辑
            refreshColor(position, holder)
        }

        override fun BaseQuickBindingHolder<FragmentOverviewEpItemBinding>.converted(item: EpNoEntity) {
            binding.tvEp.text = item.number
        }

        private fun refreshColor(
            position: Int,
            holder: BaseQuickBindingHolder<FragmentOverviewEpItemBinding>,
        ) {
            if (position <= selectIndex) {
                holder.binding.tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
                holder.binding.tvEp.backgroundTintList =
                    context.getColor(CommonColor.save_collect).tint
            } else {
                holder.binding.tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                holder.binding.tvEp.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
            }
        }
    }

    companion object {
        private const val PAYLOAD_REFRESH_COLOR = 1

        fun show(
            fragmentManager: FragmentManager,
            mediaProgress: EpSaveProgress,
            onSaveListener: ((Int) -> Unit)? = null,
        ) {
            MediaEpActionDialog().apply {
                this.onSaveListener = onSaveListener
                this.arguments = bundleOf(NavKey.KEY_PARCELABLE to mediaProgress)
            }.show(fragmentManager, "MediaEpActionDialog")
        }
    }
}*/
