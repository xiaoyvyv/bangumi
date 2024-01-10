package com.xiaoyv.bangumi.ui.media.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.xiaoyv.bangumi.databinding.ActivityMediaDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaIndexActionDialog
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.FixHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.helper.callback.AutoHideTitleListener
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.copyText
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import kotlinx.coroutines.delay

/**
 * Class: [MediaDetailActivity]
 *
 * @author why
 * @since 11/24/23
 */
class MediaDetailActivity :
    BaseViewModelActivity<ActivityMediaDetailBinding, MediaDetailViewModel>() {

    private val vpAdapter by lazy {
        MediaDetailAdapter(supportFragmentManager, lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mediaId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.mediaName = bundle.getString(NavKey.KEY_STRING_THIRD).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
        binding.toolbar.title = viewModel.mediaName

        FixHelper.fixCool(binding.ivBanner, binding.toolbarLayout, 204.dpi)

        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(CommonId.fab_new_blog, CommonDrawable.ic_post_add)
                .setFabBackgroundColor(getAttrColor(GoogleAttr.colorPrimary))
                .setFabImageTintColor(getAttrColor(GoogleAttr.colorOnPrimary))
                .setLabel("发布日志影评")
                .setLabelBackgroundColor(getAttrColor(GoogleAttr.colorPrimarySurface))
                .setLabelColor(getAttrColor(GoogleAttr.colorOnPrimary))
                .setLabelClickable(false)
                .create()
        )

        binding.speedDial.addActionItem(
            SpeedDialActionItem.Builder(CommonId.fab_new_topic, CommonDrawable.ic_add_comment)
                .setFabBackgroundColor(getAttrColor(GoogleAttr.colorPrimary))
                .setFabImageTintColor(getAttrColor(GoogleAttr.colorOnPrimary))
                .setLabel("发布话题讨论")
                .setLabelBackgroundColor(getAttrColor(GoogleAttr.colorPrimarySurface))
                .setLabelColor(getAttrColor(GoogleAttr.colorOnPrimary))
                .setLabelClickable(false)
                .create()
        )
    }


    override fun initData() {
        vpAdapter.mediaId = viewModel.mediaId

        binding.vpContent.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount
        binding.vpContent.adapter = vpAdapter

        tabLayoutMediator.attach()
    }

    override fun initListener() {
        binding.appBar.addOnOffsetChangedListener(AutoHideTitleListener(binding.toolbar) { viewModel.requireMediaName })

        binding.speedDial.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            binding.speedDial.close()

            if (!UserHelper.isLogin) {
                RouteHelper.jumpLogin()
                return@OnActionSelectedListener false
            }

            // 条目锁定
            if (viewModel.requireNotLocked.not()) {
                showLockTip()
                return@OnActionSelectedListener false
            }

            // 构建简单的关联模型
            val detailEntity =
                viewModel.onMediaDetailLiveData.value ?: return@OnActionSelectedListener true
            val postAttach = PostAttach(
                image = detailEntity.cover,
                title = detailEntity.titleCn.ifBlank { detailEntity.titleNative },
                id = detailEntity.id,
                type = detailEntity.mediaType
            )

            // 等待动画关闭后再跳转，避免掉帧
            launchUI {
                delay(200)
                when (actionItem.id) {
                    CommonId.fab_new_blog -> RouteHelper.jumpPostBlog(postAttach)
                    CommonId.fab_new_topic -> {
                        // 注意，发布话题这里 type 要改成 TopicType.TYPE_SUBJECT
                        postAttach.type = TopicType.TYPE_SUBJECT
                        RouteHelper.jumpPostTopic(postAttach)
                    }
                }
            }
            true
        })

        binding.tvLocked.setOnFastLimitClickListener {
            showLockTip()
        }

        // 大图
        binding.ivCover.setOnFastLimitClickListener {
            if (viewModel.onMediaDetailLiveData.value != null) {
                RouteHelper.jumpPreviewImage(viewModel.onMediaDetailLiveData.value?.coverLarge.orEmpty())
            }
        }

        // 复制标题
        binding.tvTitle.setOnLongClickListener {
            copyText(binding.tvTitle.text.toString())
            true
        }

        // 复制子标题
        binding.tvSubtitle.setOnLongClickListener {
            copyText(binding.tvSubtitle.text.toString())
            true
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onMediaDetailLiveData.observe(this) {
            if (it == null) return@observe
            viewModel.queryMalInfo()

            binding.ivCover.loadImageAnimate(it.cover)
            binding.ivBanner.loadImageBlur(it.cover)

            binding.toolbar.title = it.titleCn.ifBlank { it.titleNative }
            binding.tvTitle.text = it.titleCn.ifBlank { it.titleNative }
            binding.tvSubtitle.text = it.titleNative

            // 是否锁定了
            binding.tvLocked.isVisible = it.locked

            // 全站评分
            binding.tvScore.isVisible = it.rating.globalRating != 0f
            binding.tvScore.text = String.format("%.1f", it.rating.globalRating)
            binding.tvScoreTip.isVisible = it.rating.description.isNotBlank()
            binding.tvScoreTip.text = it.rating.description

            // 排行榜
            binding.tvRank.text = String.format("No. %d", it.rating.globalRank)
            binding.tvRank.isVisible = it.rating.globalRank != 0

            // 好友评分
            binding.tvScoreFriendTip.isVisible = it.friendRating.score != 0f
            binding.tvScoreFriendTip.text = String.format("好友：%.1f", it.friendRating.score)

            if (it.subtype.isNotBlank()) {
                binding.tvTime.text = String.format("(%s - %s)", it.time, it.subtype)
            } else {
                binding.tvTime.text = String.format("(%s)", it.time)
            }

            invalidateOptionsMenu()
        }

        // MAL 评分
        viewModel.onMalItemLiveData.observe(this) {
            it ?: return@observe
            val score = it.payload?.score?.toFloatOrNull() ?: 0f

            binding.tvScoreMalTip.isVisible = score != 0f
            binding.tvScoreMalTip.text = String.format("MAL：%.1f", score)
        }

        viewModel.vpEnableLiveData.observe(this) {
            binding.vpContent.isUserInputEnabled = it
        }

        viewModel.vpCurrentItemType.observe(this) { type ->
            val index = vpAdapter.tabs.indexOfFirst { it.type == type }
            if (index != -1) {
                binding.vpContent.setCurrentItem(index, true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.requireNotLocked) {
            menu.add("添加目录")
                .setIcon(CommonDrawable.ic_add_index)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    MediaIndexActionDialog.show(
                        supportFragmentManager,
                        viewModel.mediaId,
                        viewModel.requireMediaName
                    )
                    true
                }

            menu.add("MAL 详情")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    viewModel.queryMalInfo()
                    true
                }
        }
        menu.addCommonMenu(BgmApiManager.buildReferer(BgmPathType.TYPE_SUBJECT, viewModel.mediaId))
        return super.onCreateOptionsMenu(menu)
    }

    private fun showLockTip() {
        showConfirmDialog(
            message = "不符合收录原则，条目及相关收藏、讨论、关联等内容将会随时被移除。",
            cancelText = null
        )
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

}