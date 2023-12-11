package com.xiaoyv.bangumi.ui.feature.magi.question

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.FragmentMagiQuestionLastBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams

/**
 * Class: [MagiQuestionDialog]
 *
 * @author why
 * @since 12/11/23
 */
class MagiQuestionDialog : DialogFragment() {
    private val lastQuestionAdapter by lazy { MagiQuestionAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMagiQuestionLastBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMagiQuestionLastBinding.bind(view)
        val entity: MagiQuestionEntity = arguments?.getParcelObj(NavKey.KEY_PARCELABLE) ?: return

        binding.rvQuestion.itemAnimator = null
        binding.rvQuestion.adapter = lastQuestionAdapter
        binding.tvSection.title = entity.lastQuestionTitle
        binding.tvSection.more = ""
        binding.tvRate.isVisible = entity.lastQuestionId.isNotBlank()

        SpanUtils.with(binding.tvRate)
            .apply {
                if (entity.userId.isNotBlank()) {
                    append("来自：")
                    append(entity.userName)
                    setClickSpan(requireContext().getAttrColor(GoogleAttr.colorPrimary), false) {
                        RouteHelper.jumpUserDetail(entity.userId)
                    }
                    append("\u3000")
                }
            }
            .append("通过率: ")
            .append(entity.lastQuestionRightRate)
            .setForegroundColor(requireContext().getAttrColor(GoogleAttr.colorPrimary))
            .append(entity.lastQuestionCount)
            .create()

        lastQuestionAdapter.submitList(entity.lastQuestionOptions)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0.25f)
        window.updateWindowParams {
            width = ScreenUtils.getScreenWidth() - 32.dpi
            gravity = Gravity.CENTER
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, answer: MagiQuestionEntity) {
            MagiQuestionDialog()
                .apply {
                    arguments = bundleOf(NavKey.KEY_PARCELABLE to answer)
                }
                .show(fragmentManager, "MagiQuestionDialog")
        }
    }
}