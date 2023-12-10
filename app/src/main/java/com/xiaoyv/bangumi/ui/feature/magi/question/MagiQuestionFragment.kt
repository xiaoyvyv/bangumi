package com.xiaoyv.bangumi.ui.feature.magi.question

import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMagiQuestionBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MagiQuestionFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiQuestionFragment :
    BaseViewModelFragment<FragmentMagiQuestionBinding, MagiQuestionViewModel>() {
    private val questionAdapter by lazy { MagiQuestionAdapter() }
    private val lastQuestionAdapter by lazy { MagiQuestionAdapter() }
    private val viewPool by lazy { RecycledViewPool() }

    override fun initView() {
        binding.tvSection.more = ""
        binding.tvSection.title = ""

        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(requireContext().getAttrColor(GoogleAttr.colorPrimary))
    }


    override fun initData() {
        binding.rvQuestion.itemAnimator = null
        binding.rvQuestion.setRecycledViewPool(viewPool)
        binding.rvQuestion.adapter = questionAdapter

        binding.rvLastQuestion.itemAnimator = null
        binding.rvLastQuestion.setRecycledViewPool(viewPool)
        binding.rvLastQuestion.adapter = lastQuestionAdapter

        viewModel.queryQuestion()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryQuestion()
        }

        questionAdapter.addOnItemChildClickListener(R.id.item_option) { _, _, position ->
            val item = questionAdapter.getItem(position) ?: return@addOnItemChildClickListener
            questionAdapter.select(item)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { !binding.srlRefresh.isRefreshing },
        )

        viewModel.onMagiQuestionLiveData.observe(this) {
            val entity = it ?: return@observe
            debugLog { entity.toJson(true) }

            binding.ivAvatar.isVisible = true
            binding.ivAvatar.loadImageAnimate(entity.userAvatar)
            binding.tvUser.text = entity.userName
            binding.tvSection.title = buildString {
                append("Magi 问答：")
                append(entity.title)
            }
            binding.tvId.text = buildString {
                append("#")
                append(entity.id)
            }

            questionAdapter.submitList(entity.options)
            lastQuestionAdapter.submitList(entity.lastQuestionOptions)

            binding.ivAvatar.setOnFastLimitClickListener {
                RouteHelper.jumpUserDetail(entity.userId)
            }
        }
    }

    companion object {
        fun newInstance(): MagiQuestionFragment {
            return MagiQuestionFragment()
        }
    }
}