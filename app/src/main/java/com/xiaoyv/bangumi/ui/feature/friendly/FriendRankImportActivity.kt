package com.xiaoyv.bangumi.ui.feature.friendly

import android.view.MenuItem
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityFriendRankBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

class FriendRankImportActivity :
    BaseViewModelActivity<ActivityFriendRankBinding, FriendRankImportViewModel>() {
    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        // 拉取中禁止返回
        onBackPressedDispatcher.addCallback {
            if (viewModel.runningLiveData.value == true) {
                showConfirmDialog(
                    title = "警告",
                    message = "正在拉取好友评分数据生成友评榜，取消将会导致生成失败或数据不全，是否退出？",
                    onConfirmClick = {
                        isEnabled = false
                        finish()
                    }
                )
                return@addCallback
            }
            isEnabled = false
            finish()
        }
    }

    override fun initListener() {
        binding.btnStart.setOnFastLimitClickListener {
            viewModel.startFetchFriendSubjects()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState
        )

        viewModel.onCompleteLiveData.observe(this) {
            val complete = it ?: return@observe
            if (complete) {
                RouteHelper.jumpFriendRankDetail()
                finish()
            }
        }

        viewModel.runningLiveData.observe(this) {
            binding.btnStart.isEnabled = !it
            binding.tvLog.isVisible = it

            if (it) {
                binding.tvLocked.text = "正在拉取中，请稍等..."
            }
        }

        viewModel.localStatsLiveData.observe(this) {
            val statuses = it.orEmpty()

            binding.tvLocked.text = buildString {
                append("当前数据库内容：包含 ")
                append(statuses.size.toString())
                append(" 个好友的数据，共 ")
                append(statuses.sumOf { status -> status.scoreCount }.toString())
                append(" 条打分")
            }
        }

        viewModel.onFriendsLiveData.observe(this) {
            val entities = it.orEmpty()

            binding.tvFriend.text = buildString {
                append("你的好友数目：")
                append(entities.size)
                append("（包含自己）")
            }

            binding.pbFriend.max = entities.size
            binding.pbFriend.setProgress(entities.size, true)
        }

        viewModel.onTotalProgressLiveData.observe(this) {
            binding.tvProgress.text = buildString {
                append("好友评分条目载入进度")
                append("：")
                append(it.first.toString())
                append("/")
                append(it.second.toString())
            }

            binding.pbProgress.max = it.second
            binding.pbProgress.setProgress(it.first, true)
        }

        viewModel.onHandingLiveData.observe(this) {
            binding.tvLog.text = it?.entries.orEmpty().joinToString("\n") { entity ->
                val current = entity.value.first
                val total = entity.value.second
                val percent = if (total == 0) 0f else current / total.toFloat() * 100f

                buildString {
                    append("用户（")
                    append(entity.key)
                    append("）")
                    append("：")
                    append(current)
                    append("/")
                    append(total)
                    append("，进度：")
                    append(String.format("%.2f%%", percent))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}