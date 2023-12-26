package com.xiaoyv.bangumi.ui.timeline.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.SnackbarUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentTimelineDialogBinding
import com.xiaoyv.bangumi.databinding.FragmentTimelineDialogItemBinding
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.entity.TimelineReplyEntity
import com.xiaoyv.common.api.parser.impl.parserTimelineSayReply
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [TimelineReplyDialog]
 *
 * @author why
 * @since 12/23/23
 */
class TimelineReplyDialog : BottomSheetDialogFragment() {
    private val itemAdapter by lazy { ItemAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentTimelineDialogBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val entity = arguments?.getParcelObj<TimelineEntity>(NavKey.KEY_PARCELABLE) ?: return
        initView(FragmentTimelineDialogBinding.bind(view), entity)
    }

    private fun initView(binding: FragmentTimelineDialogBinding, entity: TimelineEntity) {
        TimelinePageAdapter.onBindText(binding.layoutTimeline, entity, false)
        itemAdapter.isTotalTimeline = entity.isTotalTimeline

        binding.rvContent.adapter = itemAdapter
        binding.sectionComment.title = "回复"
        binding.sectionComment.more = null
        binding.stateView.showLoading()

        queryTimelineReply(binding, entity.commentUserId, entity.id)

        binding.layoutTimeline.root.setOnFastLimitClickListener {
            if (!entity.isTotalTimeline) {
                showSubReply(binding, entity.id, entity.commentUserId, entity.name, "")
            }
        }

        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_comment) {
            if (!entity.isTotalTimeline) {
                showSubReply(binding, entity.id, entity.commentUserId, entity.name, it.id)
            }
        }
    }

    /**
     * 回复吐槽
     */
    private fun showSubReply(
        binding: FragmentTimelineDialogBinding,
        timelineId: String,
        userId: String,
        userUserName: String,
        atUserId: String,
    ) {
        if (!UserHelper.isLogin) {
            toast("你还没有登录呢")
            return
        }

        requireActivity().showInputDialog(
            title = "$userUserName 的吐槽",
            inputHint = "吐槽回复",
            default = if (atUserId.isNotBlank()) "@$atUserId " else "",
            onInput = { content ->
                launchUI(
                    error = {
                        it.printStackTrace()
                        showSnackBar(it.errorMsg, SnackbarUtils.LENGTH_SHORT)
                    },
                    block = {
                        showSnackBar("正在回复中...")
                        withContext(Dispatchers.IO) {
                            BgmApiManager.bgmWebApi.postTimelineReply(
                                tmlId = timelineId,
                                content = content,
                                formHash = UserHelper.formHash
                            )
                        }

                        hideSnackBar()

                        // 刷新
                        queryTimelineReply(binding, userId, timelineId)
                    }
                )
            }
        )
    }

    /**
     * 查询时间线回复
     */
    private fun queryTimelineReply(
        binding: FragmentTimelineDialogBinding,
        userId: String,
        timelineId: String,
    ) {
        launchUI(
            error = {
                it.printStackTrace()

                binding.stateView.showTip(message = it.errorMsg)
            },
            block = {
                binding.stateView.showLoading()

                val list = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryTimelineReply(userId, timelineId)
                        .parserTimelineSayReply()
                }
                require(list.isNotEmpty()) { "暂时没有回复" }

                itemAdapter.submitList(list)

                binding.stateView.showContent()
            }
        )
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<TimelineReplyEntity,
            FragmentTimelineDialogItemBinding>(IdDiffItemCallback()) {
        var isTotalTimeline: Boolean = false

        override fun BaseQuickBindingHolder<FragmentTimelineDialogItemBinding>.converted(item: TimelineReplyEntity) {
            binding.tvContent.text = item.content
            binding.tvContent.movementMethod = LinkMovementMethodCompat.getInstance()
            binding.ivComment.isVisible = isTotalTimeline.not()
        }
    }

    companion object {
        fun show(childFragmentManager: FragmentManager, entity: TimelineEntity) {
            TimelineReplyDialog().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to entity)
            }.show(childFragmentManager, "TimelineReplyDialog")
        }
    }
}