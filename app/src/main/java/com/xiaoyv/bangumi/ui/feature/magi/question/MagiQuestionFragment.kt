package com.xiaoyv.bangumi.ui.feature.magi.question

import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMagiQuestionBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.annotation.MagiType
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.useNotNull
import kotlinx.coroutines.delay

/**
 * Class: [MagiQuestionFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiQuestionFragment :
    BaseViewModelFragment<FragmentMagiQuestionBinding, MagiQuestionViewModel>() {
    private val questionAdapter by lazy { MagiQuestionAdapter() }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(requireContext().getAttrColor(GoogleAttr.colorPrimary))
    }


    override fun initData() {
        binding.rvQuestion.itemAnimator = null
        binding.rvQuestion.adapter = questionAdapter
        viewModel.queryQuestion()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryQuestion()
        }

        questionAdapter.addOnItemChildClickListener(R.id.item_option) { _, _, position ->
            val item = questionAdapter.getItem(position) ?: return@addOnItemChildClickListener
            questionAdapter.select(item)

            viewModel.submitAnswer(item)
        }

        binding.btnEnter.setOnFastLimitClickListener {
            showLastAnswer()
        }

        binding.ivAvatar.setOnFastLimitClickListener {
            useNotNull(viewModel.onMagiQuestionLiveData.value) {
                RouteHelper.jumpUserDetail(userId)
            }
        }

        binding.tvType.setOnFastLimitClickListener {
            val items = MagiType.items()
            requireActivity().showOptionsDialog(
                title = "选取问答类型",
                items = items.map { pair -> pair.second },
                onItemClick = { _, which ->
                    binding.srlRefresh.isRefreshing = true
                    viewModel.magiType = items[which].first
                    viewModel.queryQuestion()
                }
            )
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { !binding.srlRefresh.isRefreshing },
            canShowTip = { viewModel.isRefresh }
        )

        viewModel.onMagiQuestionLiveData.observe(this) {
            val entity = it ?: return@observe

            binding.ivAvatar.isVisible = true
            binding.ivAvatar.loadImageAnimate(entity.userAvatar)
            binding.tvUser.text = entity.userName
            binding.tvQuestion.text = entity.title
            binding.tvType.text = MagiType.string(viewModel.magiType)
            binding.tvType.isVisible = true

            binding.tvId.text = buildString {
                append("#")
                append(entity.id)
            }

            questionAdapter.selectIndex = -1
            questionAdapter.submitList(entity.options)

            binding.btnEnter.isVisible = entity.lastQuestionId.isNotBlank()

            // 显示回答答案
            if (entity.lastQuestionId.isNotBlank() && viewModel.isRefresh.not()) {
                launchUI {
                    delay(100)
                    showLastAnswer()
                }
            }
        }
    }

    private fun showLastAnswer() {
        useNotNull(viewModel.onMagiQuestionLiveData.value) {
            if (lastQuestionId.isNotBlank()) {
                // 这里 userId 已经变成下一题用户了，仅显示上题答案，移除 userId
                MagiQuestionDialog.show(childFragmentManager, copy(userId = ""))
            }
        }
    }

    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireContext())
    }

    companion object {
        fun newInstance(): MagiQuestionFragment {
            return MagiQuestionFragment()
        }
    }
}